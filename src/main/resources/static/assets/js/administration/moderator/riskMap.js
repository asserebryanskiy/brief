const SEND_ANSWER_PHASE = '0_1';

if (getRoundOrder($('.round.active').attr('id')) === 0) {
    $('.sector-count-bar').hide();
    $('.round-nav-1').hide();
} else {
    $('.round-nav-0').hide();
}

controller.setOnNextRound(() => {
    if (getRoundOrder($('.round.active').attr('id')) === 0) {
        $('.sector-count-bar').show();
        $('.round-nav-0').hide();
        $('.round-nav-1').show();
        $('#round-0').hide();
    }

    stompClient.send('/app/' + gameSessionId + '/changeSector', {}, getRoundOrder($('.round.next').attr('id')));
});
function onWsConnect(stompClient) {

}
controller.connect();

