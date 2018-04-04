const SEND_ANSWER_PHASE = '1';

controller.nextRound = () => {
    controller.timer.clearTimers();
    setTimersOriginalValues();

    // remove all phases' classes except phase
    $('.phase').removeClass('active previous next played');

    // set first phase active and second next
    $('.phase-0').addClass('active');
    $('.phase-1').addClass('next');

    stompClient.send('/app/' + gameSessionId + '/changeRound', {}, 0);
};
function onWsConnect(stompClient) {

}
controller.connect();

