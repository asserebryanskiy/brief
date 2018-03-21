const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);
const gameSessionId = $('#game-session-id').text();
const projectorMode = $('#projector-mode').length > 0;
const controller = new GameController();

let username = '';
let currentPhaseNumber = $('#current-phase-number').text();
let currentRoundIndex = 0;
let disableTimer = false;

function GameController() {
    this.onPhaseChange = null;
    // this.onTimerChanged = null;

    this.setOnPhaseChange = (onPhaseChange) => this.onPhaseChange = onPhaseChange;
    // this.setOnTimerChanged = (onTimerChanged) => this.onTimerChanged = onTimerChanged;

    this.connect = (onWsConnect) => {
        stompClient.connect({}, function (frame) {
            username = frame.headers['user-name'];

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
                controller.changePhase(newPhaseNumber, timerStr, additional);
            }, {});

            stompClient.subscribe('/topic/' + gameSessionId + '/changeRound', (roundNumber) => {
                controller.nextRound(roundNumber.body);
            });
            stompClient.subscribe('/topic/' + gameSessionId + '/statistics', (statsList) => {
                drawChart(JSON.parse(statsList.body));
            }, {});
            stompClient.subscribe('/topic/' + gameSessionId + '/finishGame', () => {
                $('#phase-' + currentPhaseNumber).hide();
                $('.finish-game').show();
            }, {});

            // connect
            if (!projectorMode) stompClient.send("/app/connect", {}, "");

            // do game specific stuff if any
            if (onWsConnect != null) onWsConnect(stompClient);

            // show screen
            // ToDo: Если соединение прервано, нужно сообщить об этом модератору!
        });
    };

    this.changePhase = function changePhase(newPhaseNumber, timerStr, additional) {
        // enable timer in case it was disabled in previous phase
        disableTimer = false;

        newPhaseNumber = parseInt(newPhaseNumber);

        // do phase specific stuff
        if (this.onPhaseChange != null) this.onPhaseChange(newPhaseNumber, timerStr, additional);

        // change view
        $('#phase-' + currentPhaseNumber).hide();
        $('#phase-' + newPhaseNumber).show();
        currentPhaseNumber = newPhaseNumber;
        const $timer = $('.timer');
        if ($timer.is(':visible')) {
            $timer.text(timerStr);
        }
    };

    this.nextRound = function nextRound(roundNumber) {
        // set current round to new value
        currentRoundIndex = roundNumber;

        // change round-name text
        $('#round-name').text('Раунд ' + (parseInt(roundNumber) + 1));

        // enable answer send
        if (!projectorMode) enableAnswerSend(true);

        // erase all answers and correct-answer
        $('.answer-variant').removeClass('selected correct-answer');

        controller.changePhase(0, '');
    };
}

/***********************************************
 *                                             *
 *              ON CLICK FUNCTIONS             *
 *                                             *
 ************************************************/

$('.logout-svg').click(() => {
    $('.logout-popup').show();
});

$('.logout-popup-yes-btn').click(() => {
    stompClient.send("/app/logout/" + username, {}, "");
});

/***********************************************
 *                                             *
 *               HELPER FUNCTIONS              *
 *                                             *
 ************************************************/

function getTimerDurationStr(timerDuration) {
    let min = Math.floor(timerDuration / 60);
    let sec = timerDuration % 60;
    if (min < 10) min = '0' + min;
    if (sec < 10) sec = '0' + sec;
    return min + ":" + sec;
}

function notNull(obj) {
    return typeof obj !== 'undefined';
}
