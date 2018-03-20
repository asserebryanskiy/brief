const socket = new SockJS('/websocket');
let stompClient;
const gameSessionId = $('#game-session-id').text();
const projectorMode = $('#projector-mode').length > 0;

let username = '';
let currentPhaseNumber = $('#current-phase-number').text();
let currentRoundIndex = 0;
let disableTimer = false;

function Game(phasesCallbacks, roundCallbacks, onWsConnect) {
    this.phasesCallbacks = phasesCallbacks;
    this.roundCallbacks = roundCallbacks;

    stompClient = Stomp.over(socket);
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
            console.log(timerStr);
            game.changePhase(newPhaseNumber, timerStr, additional);
        }, {});

        stompClient.subscribe('/topic/' + gameSessionId + '/changeRound', (roundNumber) => {
            game.nextRound(roundNumber.body);
        });
        stompClient.subscribe('/topic/' + gameSessionId + '/statistics', (statsList) => {
            drawChart(JSON.parse(statsList.body));
        }, {});
        stompClient.subscribe('/topic/' + gameSessionId + '/finishGame', () => {
            $('#phase-' + currentPhaseNumber).hide();
            $('.finish-game').show();
        }, {});
        if (!projectorMode) stompClient.send("/app/connect", {}, "");

        if (notNull(onWsConnect)) onWsConnect(stompClient);
        // ToDo: Если соединение прервано, нужно сообщить об этом модератору!
    });

    this.changePhase = function changePhase(newPhaseNumber, timerStr, additional) {
        // enable timer in case it was disabled in previous phase
        disableTimer = false;

        newPhaseNumber = parseInt(newPhaseNumber);

        // do phase specific stuff
        if (notNull(this.phasesCallbacks)) {
            if (notNull(this.phasesCallbacks[newPhaseNumber])) {
                this.phasesCallbacks[newPhaseNumber](additional);
            }

            this.phasesCallbacks.afterAll(newPhaseNumber);
        }

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

        game.changePhase(0, '');
    };
}

function notNull(obj) {
    return typeof obj !== 'undefined';
}

function getTimerDurationStr(timerDuration) {
    let min = Math.floor(timerDuration / 60);
    let sec = timerDuration % 60;
    if (min < 10) min = '0' + min;
    if (sec < 10) sec = '0' + sec;
    return min + ":" + sec;
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

/*
// if answers were already submitted block input
if ($('#answers-submitted').length !== 0) {
    enableAnswerSend(false);
}*/
