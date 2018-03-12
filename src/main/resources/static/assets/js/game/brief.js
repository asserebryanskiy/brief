const SEND_ANSWER_PHASE = 3;
const RECEIVE_CORRECT_ANSWER_PHASE = 4;
const STATISTICS_PHASE = 5;

const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);
let username = '';
let currentPhaseNumber = parseInt($('#current-phase-number').text());
let currentRoundIndex = 0;
let gameSessionId = $('#game-session-id').text();
let disableTimer = false;

function getTimerDurationStr(timerDuration) {
    let min = Math.floor(timerDuration / 60);
    let sec = timerDuration % 60;
    if (min < 10) min = '0' + min;
    if (sec < 10) sec = '0' + sec;
    return min + ":" + sec;
}

function changePhase(newPhaseNumber, timerStr, additional) {
    disableTimer = false;
    // phase 4 is phase of showing correct answers, so we do not need to change view.
    if (parseInt(newPhaseNumber) === RECEIVE_CORRECT_ANSWER_PHASE) {
        let correctAnswer = additional;
        if (correctAnswer === '') correctAnswer = $('#correct-answer').text();
        $('p:contains("' + correctAnswer + '")').parent().addClass('correct-answer');
        setScore(correctAnswer);
    }
    if (parseInt(newPhaseNumber) === STATISTICS_PHASE) {
        drawChart();
    }
    $('#phase-' + currentPhaseNumber).hide();
    $('#phase-' + newPhaseNumber).show();
    currentPhaseNumber = parseInt(newPhaseNumber);
    const $timer = $('.timer');
    if ($timer.is(':visible')) {
        $timer.text(timerStr);
        // startTimer($timer, () => console.log('finished'));
    }
}

function nextRound(roundNumber) {
    // set current round to new value
    currentRoundIndex = roundNumber;

    // change round-name text
    $('#round-name').text('Раунд ' + (parseInt(roundNumber) + 1));

    // enable answer send
    enableAnswerSend(true);

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
    disableTimer = !value && currentPhaseNumber === SEND_ANSWER_PHASE;

    // enable answer-variant clicking
    if (value) {
        $('.answer-variant').click((event) => toggleSelected(event));
    } else {
        $('.answer-variant').unbind('click');
    }

    // enable send-responses btn
    $('#send-responses-btn').prop('disabled', !value);
}

function setScore(correctAnswer) {
    const answer = getAnswerStr();
    const numberOfVariants = answer.length / 2;
    const score = answer.includes(correctAnswer) ?
        (numberOfVariants > 3 ? 0 : 4 - numberOfVariants) : 0;
    let scoreText;
    switch (score) {
        case 0:
            scoreText = 'Вы заработали 0 баллов';
            break;
        case 1:
            scoreText = 'Вы заработали 1 балл';
            break;
        default:
            scoreText = 'Вы заработали ' + score + ' балла';
    }
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

    stompClient.subscribe('/queue/' + username + '/logout', function () {
        stompClient.disconnect();
        window.location = 'http://localhost:8080/';
    }, {});

    stompClient.subscribe('/topic/' + gameSessionId + '/nextPhase', (message) => {
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
            if ($('.phase-container:visible').attr('id') === 'phase-3') {
                if (newTimerValue === '00:00') {
                    sendResponses();
                }
            }
        }
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/additionalAnswerSendTime', () => {
        enableAnswerSend(true);
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/statistics', (statsList) => {
        drawChart(JSON.parse(statsList.body));
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/finishGame', () => {
        $('#phase-' + currentPhaseNumber).hide();
        $('.finish-game').show();
    }, {});
    stompClient.send("/app/connect", {}, "");
    // Если соединение прервано, нужно сообщить об этом модератору!
});

// show appropriate phase if page was reloaded
changePhase(currentPhaseNumber, '', '');

// if answers were already submitted block input
if ($('#answers-submitted').length !== 0) {
    enableAnswerSend(false);
}
