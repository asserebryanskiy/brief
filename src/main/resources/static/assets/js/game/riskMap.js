let answerSendEnabled = true;

function onWsConnect(stompClient) {
    stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
        const newTimerValue = message.body;
        $('.timer').text(newTimerValue);
        answerSendEnabled = newTimerValue !== '00:00';
    }, {});
}

controller.connect(onWsConnect);
controller.setOnPhaseChange((newPhaseNumber, timerStr, additional) => {
    newPhaseNumber = parseInt(newPhaseNumber);

    if (currentRoundIndex === 0 && newPhaseNumber === 1) {
        $('#phase-1').css('display', 'flex');
    }
});
controller.changePhase(currentPhaseNumber, '', '');

$('.small-img-wrapper').click((event) => {
    const $target = $(event.currentTarget);
    const imgId = $target.find('img').attr('id').substr(0, 2);
    const $popup = $target.parent().find('.popup-wrapper');
    const $popupContent = $popup.find('.popup-content');
    if ($popupContent.children('img').length === 0) {
        $('#' + imgId + '-large-img').insertBefore($popupContent.children('.answer-inputs'));
        // $popupContent.children('.answer-inputs').insertAf.append($('#' + imgId + '-large-img'))
    }
    $popup.show();
});

$('.answer-input').click((event) => {
    const $target = $(event.currentTarget);
    if (answerSendEnabled) {
        // clear self-made radio-buttons
        $target.siblings().removeClass('selected');

        // toggle selection of clicked input
        $target.toggleClass('selected');

        // remove indicator from img-cell if any
        const $indicator = $target.parents('.risk-img-cell').find('.risk-indicator');
        $indicator.removeClass('low-level mid-level high-level');

        // add new indicator to img-cell
        const className = $target.hasClass('low-level') ? 'low-level'
            : $target.hasClass('no-level') ? 'no-level'
            : $target.hasClass('mid-level') ? 'mid-level' : 'high-level';
        $indicator.addClass(className);

        // send response to server
        const answer = getAnswerStr();
        stompClient.send('/app/responses', {}, JSON.stringify({'username':username, 'answerStr':answer}));
    }
});

function getAnswerStr() {
    let answerStr = '';
    $('.risk-img-cell').each((i, el) => {
        const $indicator = $(el).find('.risk-indicator');
        if ($indicator.hasClass('low-level')) answerStr += i + '-0,';
        if ($indicator.hasClass('mid-level')) answerStr += i + '-1,';
        if ($indicator.hasClass('high-level')) answerStr += i + '-2,';

        // delete last comma
        if (i === 11 && answerStr.length > 0) answerStr = answerStr.substr(0, answerStr.length - 1);
    });
    return answerStr;
}