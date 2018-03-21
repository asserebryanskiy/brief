const SEND_ANSWER_PHASE = 3;
const RECEIVE_CORRECT_ANSWER_PHASE = 4;
const STATISTICS_PHASE = 5;

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

    // enableAnswerSend(false);
    $('.flash').slideDown(800).delay(1000).slideUp(800);
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
 *        CONTROLLER SETTINGS       *
 ************************************/

controller.setOnPhaseChange((newPhaseNumber, timerStr, additional) => {
    newPhaseNumber = parseInt(newPhaseNumber);

    switch(newPhaseNumber) {
        case SEND_ANSWER_PHASE:
            if (!projectorMode) enableAnswerSend(true);
            break;
        case RECEIVE_CORRECT_ANSWER_PHASE:
            enableAnswerSend(false);
            let correctAnswer = additional;
            if (correctAnswer === '') correctAnswer = $('#correct-answer').text();
            $('p:contains("' + correctAnswer + '")').parent().addClass('correct-answer');
            if (!projectorMode) setScore(correctAnswer);
            break;
        case STATISTICS_PHASE:
            drawChart();
            break;
    }

    if (newPhaseNumber < RECEIVE_CORRECT_ANSWER_PHASE) {
        $('.correct-answer').removeClass('correct-answer');
    }
});

function onWsConnect(stompClient) {
    stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
        const newTimerValue = message.body;
        if (!disableTimer) {
            $('.timer').text(newTimerValue);
            // phase-3 is send responses phase
            if ($('.phase-container:visible').attr('id') === 'phase-' + SEND_ANSWER_PHASE
                && !projectorMode) {
                if (newTimerValue === '00:00') {
                    sendResponses();
                    enableAnswerSend(false);
                }
            }
        }
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/additionalAnswerSendTime', () => {
        if (!projectorMode) enableAnswerSend(true);
    }, {});
    stompClient.subscribe('/topic/' + gameSessionId + '/changePhase', (message) => {
        const newPhaseNumber = parseInt(JSON.parse(message.body).phaseNumber);
        if (newPhaseNumber === RECEIVE_CORRECT_ANSWER_PHASE) sendResponses();
    }, {});
}
controller.connect(onWsConnect);
controller.changePhase(currentPhaseNumber, '', '');

// if answers were already submitted block input
if ($('#answers-submitted').length !== 0) {
    enableAnswerSend(false);
}
