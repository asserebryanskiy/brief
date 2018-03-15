const SEND_ANSWER_PHASE = 3;
const RECEIVE_CORRECT_ANSWER_PHASE = 4;
const STATISTICS_PHASE = 5;

const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);
const gameSessionId = $('#game-session-id').text();
const projectorMode = $('#projector-mode').length > 0;

let username = '';
let currentPhaseNumber = $('#current-phase-number').text();
let currentRoundIndex = 0;
let disableTimer = false;

function getTimerDurationStr(timerDuration) {
    let min = Math.floor(timerDuration / 60);
    let sec = timerDuration % 60;
    if (min < 10) min = '0' + min;
    if (sec < 10) sec = '0' + sec;
    return min + ":" + sec;
}

function changePhase(newPhaseNumber, timerStr, additional) {
    // enable timer in case it was disabled in previous phase
    disableTimer = false;

    // do phase specific stuff
    switch (parseInt(newPhaseNumber)) {
        case SEND_ANSWER_PHASE:
            if (!projectorMode) enableAnswerSend(true);
            break;
        case RECEIVE_CORRECT_ANSWER_PHASE:
            let correctAnswer = additional;
            if (correctAnswer === '') correctAnswer = $('#correct-answer').text();
            $('p:contains("' + correctAnswer + '")').parent().addClass('correct-answer');
            if (!projectorMode) setScore(correctAnswer);
            break;
        case STATISTICS_PHASE:
            drawChart();
            break;
    }

    // if phase is earlier than RECEIVE-CORRECT-ANSWER than erase correct answer
    if (parseInt(newPhaseNumber) < RECEIVE_CORRECT_ANSWER_PHASE) {
        $('.correct-answer').removeClass('correct-answer');
    }

    // change view
    $('#phase-' + currentPhaseNumber).hide();
    $('#phase-' + newPhaseNumber).show();
    currentPhaseNumber = newPhaseNumber;
    const $timer = $('.timer');
    if ($timer.is(':visible')) {
        $timer.text(timerStr);
    }
}

function nextRound(roundNumber) {
    // set current round to new value
    currentRoundIndex = roundNumber;

    // change round-name text
    $('#round-name').text('Раунд ' + (parseInt(roundNumber) + 1));

    // enable answer send
    if (!projectorMode) enableAnswerSend(true);

    // erase all answers and correct-answer
    $('.answer-variant').removeClass('selected correct-answer');

    changePhase(0, '');
}

/************************************
 *            HELP POPUP            *
 ************************************/

$('.answer-calc-help').click(() => $('.help-popup').show());
$('.help-popup-back').click(() => $('.help-popup').hide());
$('#close-popup-btn').click(() => $('.help-popup').hide());

/************************************
*          ANSWERS SUBMIT          *
************************************/

$('.answer-variant').click((event) => {
    toggleSelected(event);
});

function toggleSelected(event) {
    const $target = $(event.currentTarget);
    const answer = $target.children('.answer-text').text();
    $('.container').find('p:contains("' + answer + '")').parent().toggleClass('selected');
}

$('#send-responses-btn').click(() => {
    sendResponses();
});

function getAnswerStr() {
    return $('#phase-3').find('.selected').text().replace(/\s+/g, '');
}

function sendResponses() {
    const answer = getAnswerStr();
    // send responses
    stompClient.send('/app/responses', {}, JSON.stringify({'username':username, 'answerStr':answer}));

    enableAnswerSend(false);
}

function enableAnswerSend(value) {
    // enable timer
    // 3 - is number of send answers phase
    disableTimer = !value && currentPhaseNumber === SEND_ANSWER_PHASE;

    // enable answer-variant clicking
    const $cell = $('.answer-variant');
    if (value) {
        // because if round was skipped than there are two opposite handlers on click
        $cell.unbind('click');
        $cell.click((event) => toggleSelected(event));
    } else {
        $cell.unbind('click');
    }

    // enable send-responses btn
    $('#send-responses-btn').prop('disabled', !value);
}

function setScore(correctAnswer) {
    const answer = getAnswerStr();
    const numberOfVariants = answer.length / 2;

    // compute score
    /*let score = answer.includes(correctAnswer) ?
        (numberOfVariants > 3 ? 0 : 4 - numberOfVariants) : 0;*/
    let score;
    if (!answer.includes(correctAnswer)) score = 0;
    else {
        if      (numberOfVariants === 1) score = 15;
        else if (numberOfVariants === 2) score = 10;
        else if (numberOfVariants < 5)   score = 5;
        else if (numberOfVariants < 9)   score = 2;
        else                             score = 0;
    }

    // construct message to a user
    let scoreText;
    if (score > 2 || score === 0) scoreText = 'Вы заработали ' + score + ' баллов.';
    else scoreText = 'Вы заработали 2 балла';
    $('.score-text').text(scoreText);
}

/************************************
 *         STATISTICS CHART         *
 ************************************/



/************************************
 *         ON DOCUMENT LOAD         *
 ************************************/

stompClient.connect({}, function (frame) {
    username = frame.headers['user-name'];
    console.log(username);

    if (!projectorMode) {
        stompClient.subscribe('/queue/' + username + '/logout', function () {
            stompClient.disconnect();
            const url = window.location.href;
            window.location = url.slice(0, url.lastIndexOf('/'));
        }, {});
    }

    stompClient.subscribe('/topic/' + gameSessionId + '/changePhase', (message) => {
        const newPhaseNumber = JSON.parse(message.body).phaseNumber;
        const timerStr = getTimerDurationStr(JSON.parse(message.body).timerDuration);
        const additional = JSON.parse(message.body).additional;
        console.log(timerStr);
        changePhase(newPhaseNumber, timerStr, additional);
    }, {});

    stompClient.subscribe('/topic/' + gameSessionId + '/changeRound', (roundNumber) => {
        nextRound(roundNumber.body);
    });

    stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
        const newTimerValue = message.body;
        if (!disableTimer) {
            $('.timer').text(newTimerValue);
            // phase-3 is send responses phase
            if ($('.phase-container:visible').attr('id') === 'phase-' + SEND_ANSWER_PHASE
                && !projectorMode) {
                if (newTimerValue === '00:00') {
                    sendResponses();
                }
            }
        }
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/additionalAnswerSendTime', () => {
        if (!projectorMode) enableAnswerSend(true);
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/statistics', (statsList) => {
        drawChart(JSON.parse(statsList.body));
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/finishGame', () => {
        $('#phase-' + currentPhaseNumber).hide();
        $('.finish-game').show();
    }, {});
    if (!projectorMode) stompClient.send("/app/connect", {}, "");
    // ToDo: Если соединение прервано, нужно сообщить об этом модератору!
});

// show appropriate phase if page was reloaded
changePhase(currentPhaseNumber, '', '');

// if answers were already submitted block input
if ($('#answers-submitted').length !== 0) {
    enableAnswerSend(false);
}
