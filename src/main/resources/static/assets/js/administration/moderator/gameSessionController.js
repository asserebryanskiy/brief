// web-socket constants
const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);
const controller = new GameSessionController();

const href = window.location.href;
const gameSessionId = href.substr(href.lastIndexOf('/') + 1);

function GameSessionController() {
    // FIELDS
    this.timer = new Timer(stompClient);
    this.onNextPhase = null;
    this.onPrevPhase = null;
    this.onNextRound = null;
    this.$lastPhase = null;

    // FUNCTIONS
    this.setOnNextPhase = (func) => {
        this.onNextPhase = func;
    };

    this.setOnPrevPhase = (func) => {
        this.onPrevPhase = func;
    };

    this.setOnNextRound = (func) => {
        this.onNextRound = func;
    };

    this.setLastPhase = ($phase) => {
        this.$lastPhase = $phase;
    };

    /**
     * Calls next phase of the game: exchanges classes, starts timer, changes view.
     * */
    this.nextPhase = () => {
        // clear timers if any
        this.timer.clearTimers();

        // set previous phase to played
        $('.phase.previous:visible').removeClass('previous').addClass('played');

        // set active phase to previous
        $('.phase.active:visible').removeClass('active').addClass('previous');

        // set this phase to active
        const $newActive = $('.phase.next:visible');
        $newActive.removeClass('next').addClass('active');

        // set next to it phase to next
        const phaseOrder = getPhaseOrder($newActive.attr('id'));
        const $nextPhase = getPhaseNear($newActive, 1);
        if ($nextPhase.exists()) {
            $nextPhase.addClass('next');
        } else {
            controller.nextRound();
            return;
        }

        console.log(this.onNextPhase);
        if (this.onNextPhase !== null) this.onNextPhase(phaseOrder);

        // establish timer if it exists
        this.notifySubscribers($newActive, phaseOrder);
    };

    this.previousPhase = () => {
        // clear timers if any
        this.timer.clearTimers();
        setTimersOriginalValues();

        // remove next class from currently next phase
        $('.phase.next:visible').removeClass('next');

        // set active phase to next
        $('.phase.active:visible').removeClass('active').addClass('next');

        // set this phase to active
        const $newActive = $('.phase.previous:visible');
        $newActive.removeClass('previous').addClass('active');

        // set next to it phase to next
        const phaseOrder = getPhaseOrder($newActive.attr('id'));
        const $prevPhase = getPhaseNear($newActive, -1);
        $prevPhase.removeClass('played').addClass('previous');

        if (this.onPrevPhase != null) this.onPrevPhase(phaseOrder);

        this.notifySubscribers($newActive, phaseOrder);
    };

    /**
     * Sends commands to all subscribers to change phase.
     * */
    this.notifySubscribers = ($newPhase, phaseOrder) => {
        const $timer = $newPhase.children('.timer');
        if ($timer.exists()) {
            // send to subscribers signal to change view
            console.log(getTimerDurationInSeconds($timer.text()));
            stompClient.send('/app/' + gameSessionId + '/changePhase', {}, JSON.stringify({
                'phaseNumber': phaseOrder,
                'timerDuration': getTimerDurationInSeconds($timer.text())
            }));
            this.timer.startTimer($timer, () => onTimerFinish($timer, $newPhase));
        } else {
            stompClient.send('/app/' + gameSessionId + '/changePhase', {}, JSON.stringify({
                'phaseNumber': phaseOrder,
            }));
        }
    };

    this.nextRound = () => {
        // clear timers if any
        this.timer.clearTimers();
        setTimersOriginalValues();

        // process gameSession specific actions
        if (this.onNextRound != null) this.onNextRound();

        // set active round to played
        $('.round.active').removeClass('active').addClass('played');

        const $newActive = $('.round.next');
        // if next round does not exist finish game
        if (!$newActive.exists()) controller.finishGame();
        else {
            // set next round to active
            $newActive.removeClass('next').addClass('active');

            // set next to new active next
            const roundId = $newActive.attr('id');
            const nextRoundId = 'round-' + (getRoundOrder(roundId) + 1);
            const $nextRound = $('#' + nextRoundId);
            if ($nextRound.exists()) {
                $nextRound.addClass('next');
            } else {
                if (this.$lastPhase !== null) this.$lastPhase.find('.phase-name').text('Завершить игру')
            }
            stompClient.send('/app/' + gameSessionId + '/changeRound', {}, getRoundOrder(roundId));
        }
    };

    this.finishGame = () => {
        stompClient.send('/topic/' + gameSessionId + '/finishGame', {}, '');
    };

    /***********************************************
     *                                             *
     *            STOMP CLIENT SETTINGS            *
     *                                             *
     ************************************************/

    this.connect = (onWsConnect) => {
        stompClient.connect({}, function () {
            stompClient.subscribe('/queue/' + gameSessionId + '/connection', function (message) {
                const mes = message.body;
                const divider = mes.indexOf(' ');
                const command = mes.slice(0, divider);
                const playerId = mes.substr(divider + 1);
                const player = $('#' + playerId);
                const playerRow = $('#player-row-' + playerId);
                switch (command) {
                    case 'Connect':
                        player.removeClass('disconnected').addClass('connected');
                        playerRow.removeClass('disconnected').addClass('connected-row');
                        playerRow.children('.connection-td').text('Connected');
                        break;
                    case 'Logout':
                        player.removeClass('connected');
                        playerRow.removeClass('connected-row');
                        playerRow.children('.connection-td').text('Disconnected');
                        break;
                    case 'Disconnected':
                        player.removeClass('connected').addClass('disconnected');
                        playerRow.removeClass('connected').addClass('disconnected');
                        break;
                }
            });

            const $phase = $('.phase.active:visible');
            // if page is reloaded and timer was running proceed running
            if ($('.timer-is-running').exists()) {
                controller.timer.startTimer($phase.children('.timer'),
                    () => onTimerFinish($phase.children('.timer'), $phase));
            }
            // if current phase has timer and page was reloaded after timer finished, show +30 sec btn
            if ($('.timer-finished').exists()) {
                $phase.children('.timer').hide();
                $phase.children('.add-30-sec-btn').show();
            }

            // do game specific stuff
            if (onWsConnect !== null && typeof onWsConnect !== 'undefined') onWsConnect(stompClient);

            // show screen
        });
    };
}

/***********************************************
 *                                             *
 *              ON CLICK FUNCTIONS             *
 *                                             *
 ************************************************/

$('.round').click(function (event) {
    const stage = event.currentTarget;
    if ($(stage).hasClass('next')) {
        // check if next phase is last. if not show confirmation popup
        if (getPhaseOrder($('.phase.next').attr('id')) === $('.phase').length - 1) {
            controller.nextRound();
        } else {
            $('#early-round-finish-popup').show();
        }
    }
});

$('#pass-round-btn').click(function () {
    $('#pass-round-popup').show();
});

// function on click on game over btn
$('#game-over-btn').click(function () {
    // if current round is not last
    if ($('.round.next').exists()) {
        $('#early-game-finish-popup').show();
    } else {
        // if current round is last check if current phase if last
        if ($('.phase.next').text() === 'Завершить игру') {
            controller.finishGame();
        } else {
            $('#early-game-finish-popup').show();
        }
    }
});

// if moderator added 30 seconds to timer
$('.add-30-sec-btn').click(function (event) {
    const btn = event.currentTarget;
    const $phase = $(btn).parent();
    const $timer = $phase.children('.timer');
    $(btn).hide();
    $timer.text('00:30');
    $timer.show();
    $timer.css('color', 'initial');
    controller.timer.startTimer($timer, () => onTimerFinish($timer, $phase));

    // if parent of this btn is send-responses phase inform subscribers about additional time.
    if ($phase.attr('id') === 'phase-3') {
        stompClient.send('/topic/' + gameSessionId + '/additionalAnswerSendTime');
    }
});

$('.phase').click(function (event) {
    const phase = event.currentTarget;
    if ($(phase).hasClass('next')) {
        controller.nextPhase();
    } else if ($(phase).hasClass('previous')) {
        $('#previous-phase-popup-' + getPhaseOrder(phase.id)).show();
    }
});

// on click on logout btn logout player and push him to an index page
$('.player-logout-svg').click(function (event) {
    const id = event.currentTarget.id;
    const playerId = id.slice(0, id.indexOf('-logout'));
    stompClient.send(`/app/logout/${playerId}`);
});

/***********************************************
 *                                             *
 *               POP-UP REACTIONS              *
 *                                             *
 ************************************************/

$('.early-game-finish-btn').click(function () {
    $('.confirmation-popup').hide();
    controller.finishGame();
});

$('.early-round-finish-btn').click(function () {
    $('.confirmation-popup').hide();
    controller.nextRound();
});

$('.pass-round-popup-btn').click(function () {
    $('.confirmation-popup').hide();
    controller.nextRound();
});

$('.previous-phase-popup-btn').click(function () {
    $('.confirmation-popup').hide();
    controller.previousPhase();
});

$('.confirmation-popup-back').click(function () {
    $('.confirmation-popup').hide();
});

$('.confirmation-popup-no-btn').click(function () {
    $('.confirmation-popup').hide();
});

/***********************************************
 *                                             *
 *               HELPER FUNCTIONS              *
 *                                             *
 ************************************************/

function getPhaseOrder(phaseId) {
    const roundDivInd = phaseId.indexOf('_');
    return parseInt(roundDivInd < 0 ? phaseId.substr(phaseId.indexOf('-') + 1)
                                    : phaseId.substr(roundDivInd + 1));
}

function getRoundOrder(stageId) {
    return parseInt(stageId.substr(stageId.indexOf('-') + 1));
}

function getPhaseNear($newActive, delta) {
    const phaseId = $newActive.attr('id');
    const roundDivInd = phaseId.indexOf('_');
    const phaseOrder = getPhaseOrder(phaseId);
    let nextPhaseId;
    if (roundDivInd < 0) {
        nextPhaseId = 'phase-' + ((phaseOrder + delta));
    } else {
        nextPhaseId = phaseId.slice(0, roundDivInd + 1) + ((phaseOrder + delta));
    }
    return $('#' + nextPhaseId);
}

// helper function to check if jQuery returns an element
$.fn.exists = function () {
    return this.length !== 0;
};

function notNull(obj) {
    return typeof obj !== 'undefined';
}

function onTimerFinish($timer, $phase) {
    $timer.hide();
    $phase.children('.add-30-sec-btn').show();
}

function getTimerDurationInSeconds(strDuration) {
    const delimiter = strDuration.indexOf(':');
    return parseInt(strDuration.slice(0, delimiter)) * 60   // minutes
        + parseInt(strDuration.substr(delimiter + 1));      // seconds
}

function setTimersOriginalValues() {
    $('.timer').each(function () {
        $(this).text($(this).parent().children('.timer-original-value').text());
    })
}