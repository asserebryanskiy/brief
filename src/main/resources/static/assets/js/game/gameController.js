let socket = new SockJS('/websocket');
let stompClient = Stomp.over(socket);
const gameSessionId = $('#game-session-id').text();
const projectorMode = $('#projector-mode').length > 0;
const blockAnswerInput = $('#block-answer-input').length !== 0;
const controller = new GameController();

// message constants
const sendNewAnswersText = $('#new-answers-text').text();
const changeAnswersText = $('#change-answers-text').text();
const sentAnswersText = $('#sent-answers-text').text();

let username = '';
let currentPhaseNumber = $('#current-phase-number').text();
let currentRoundIndex = 0;
let answerSendingEnabled = true;

// if answers were already submitted block input
if (blockAnswerInput) {
    controller.enableAnswerSend(false);
}

function GameController() {
    this.onPhaseChange = null;
    this.onRoundChange = null;

    this.setOnPhaseChange = (onPhaseChange) => this.onPhaseChange = onPhaseChange;
    this.setOnRoundChange = (onRoundChange) => this.onRoundChange = onRoundChange;
    // this.setOnTimerChanged = (onTimerChanged) => this.onTimerChanged = onTimerChanged;

    this.connect = (onWsConnect) => {
        function connect(frame) {
            username = frame.headers['user-name'];

            if (!projectorMode) {
                stompClient.subscribe('/queue/' + username + '/logout', function () {
                    stompClient.disconnect();
                    const url = window.location.href;
                    window.location = url.slice(0, url.lastIndexOf('/'));
                }, {});
            }

            // subscribe on timer change
            stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
                const newTimerValue = message.body;
                const $timer = $('.timer');
                $timer.text(newTimerValue);
                const div = newTimerValue.indexOf(':');
                if (parseInt(newTimerValue.slice(0, div)) === 0
                    && parseInt(newTimerValue.substr(div + 1)) < 11) {
                    $timer.addClass('warning');
                } else {
                    $timer.removeClass('warning');
                }
                // phase-3 is send responses phase
                if ($('.phase-container:visible').attr('id') === 'phase-' + SEND_ANSWER_PHASE
                    && !projectorMode) {
                    if (newTimerValue === '00:00') {
                        controller.sendResponses();
                        controller.enableAnswerSend(false);
                    }
                }
            }, {});
            stompClient.subscribe('/topic/' + gameSessionId + '/additionalAnswerSendTime', () => {
                if (!projectorMode) controller.enableAnswerSend(true);
            }, {});
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

            // // connect
            // if (!projectorMode) stompClient.send("/app/connect", {}, "");

            // do game specific stuff if any
            if (onWsConnect != null) onWsConnect(stompClient);

            // show screen

            // on connection loss (i.g when Iphone is locked) try to reconnect every 2 seconds
        }

        function reconnect() {
            let connected = false;
            let reconInv = setInterval(() => {
                socket = new SockJS('/websocket');
                stompClient = Stomp.over(socket);
                stompClient.connect({}, (frame) => {
                    clearInterval(reconInv);
                    connected = true;
                    connect(frame);
                    stompClient.send("/app/whereI", {}, "");
                    stompClient.subscribe("/queue/" + username + "/moveTo", (message) => {
                        const body = JSON.parse(message.body);
                        if (currentRoundIndex !== parseInt(message.round)) {
                            controller.nextRound(parseInt(message.round));
                        }
                        if (currentPhaseNumber !== parseInt(body.phase)) {
                            controller.changePhase(parseInt(body.phase));
                        }
                    }, {})
                }, () => {
                    if (connected) {
                        reconnect();
                    }
                });
            }, 1000);
        }

        stompClient.connect({}, (frame) => {
            connect(frame);
        }, () => {
            reconnect()
        });
    };

    this.changePhase = function changePhase(newPhaseNumber, timerStr, additional) {
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
        if (!projectorMode) controller.enableAnswerSend(true);

        // erase all answers and correct-answer
        $('.answer-variant').removeClass('selected correct-answer');

        controller.changePhase(0, '');

        // do round specific stuff
        if (this.onRoundChange != null) this.onRoundChange(roundNumber);
    };

    this.sendResponses = () => {
        const answer = getAnswerStr();
        // send responses
        stompClient.send('/app/responses', {}, JSON.stringify({'username':username, 'answerStr':answer}));

        const $btn = $('#send-responses-btn');
        if ($btn.text() === sendNewAnswersText) {
            $btn.text(changeAnswersText).addClass('change-answers');
        }
        $('.flash').slideDown(800).delay(1000).slideUp(800);
    };

    this.enableAnswerSend = (value) => {
        // enable answer-variant clicking
        const $cell = $('.answer-variant');
        const $btn = $('#send-responses-btn');
        if (value) {
            // because if round was skipped than there are two opposite handlers on click
            $cell.unbind('click');
            $cell.click((event) => toggleSelected(event));

            if (getAnswerStr().length > 0) {
                $btn.text(changeAnswersText);
                if (!$btn.hasClass('change-answers')) $btn.addClass('change-answers');
            } else {
                $btn.text(sendNewAnswersText).removeClass('change-answers');
            }
        } else {
            // if any answer is already selected change #send-responses-btn text to changeAnswersText message
            console.log('entered in unbind');
            $cell.unbind('click');
            $btn.text(sentAnswersText).removeClass('change-answers');
        }

        // enable send-responses btn
        $btn.prop('disabled', !value);

        answerSendingEnabled = value;
    };
}

/***********************************************
 *                                             *
 *              ON CLICK FUNCTIONS             *
 *                                             *
 ************************************************/

$('.logout-text').click(() => {
    $('.logout-popup').show();
});

$('.logout-popup-yes-btn').click(() => {
    stompClient.send("/app/logout/" + username, {}, "");
});

$('#send-responses-btn').click(() => {
    controller.sendResponses();
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

/***********************************************
 *                                             *
 *             WEB SOCKET SETTINGS             *
 *                                             *
 ************************************************/

// stompClient.onconnect(() => console.log('connected'));
