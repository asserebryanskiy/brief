// phase number constants
const SEND_ANSWERS_PHASE = 3;
const SEND_STATISTICS_PHASE = 5;

// web-socket constants
const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);

const href = window.location.href;
const gameSessionId = href.substr(href.lastIndexOf('/') + 1);

let timerId = 0;
let timerSubscription;

/********************************
 *  GAME-SESSION MANIPULATIONS  *
 ********************************/

/**
* Calls next phase of the game: exchanges classes, starts timer, changes view.
* */
function nextPhase() {
    // clear timers if any
    clearTimers();

    // set previous phase to played
    $('.phase.previous').removeClass('previous').addClass('played');

    // set active phase to previous
    $('.phase.active').removeClass('active').addClass('previous');

    // set this phase to active
    const $newActive = $('.phase.next');
    $newActive.removeClass('next').addClass('active');

    // set next to it phase to next
    const phaseOrder = getPhaseOrder($newActive.attr('id'));
    const nextPhaseId = 'phase-' + (((phaseOrder) + 1));
    const $nextPhase = $('#' + nextPhaseId);
    if ($nextPhase.exists()) {
        $nextPhase.addClass('next');
    } else {
        nextRound();
        return;
    }

    // if newActive order is greater than 2 show player-table
    if (phaseOrder >= SEND_ANSWERS_PHASE && !$('.players-table').is(':visible')) togglePlayersView();

    // if new phase is SEND_STATISTICS send statistics to players and projector
    if (phaseOrder === SEND_STATISTICS_PHASE) {
        stompClient.send('/app/' + gameSessionId + '/sendStatistics')
    }

    // establish timer if it exists
    notifySubscribers($newActive, phaseOrder);
}

function previousPhase() {
    // clear timers if any
    clearTimers();
    setTimersOriginalValues();

    // remove next class from currently next phase
    $('.phase.next').removeClass('next');

    // set active phase to next
    $('.phase.active').removeClass('active').addClass('next');

    // set this phase to active
    const $newActive = $('.phase.previous');
    $newActive.removeClass('previous').addClass('active');

    // set next to it phase to next
    const phaseOrder = getPhaseOrder($newActive.attr('id'));
    const previousPhaseId = 'phase-' + (((phaseOrder) - 1));
    $('#' + previousPhaseId).removeClass('played').addClass('previous');

    // if newActive order is greater than 2 show player-table
    if (phaseOrder < SEND_ANSWERS_PHASE && $('.players-table').is(':visible')) togglePlayersView();

    // if new phase is SEND_STATISTICS send statistics to players and projector
    if (phaseOrder === SEND_STATISTICS_PHASE) {
        stompClient.send('/app/' + gameSessionId + '/sendStatistics')
    }

    notifySubscribers($newActive, phaseOrder);
}
/**
 * Sends commands to all subscribers to change phase.
* */
function notifySubscribers($newPhase, phaseOrder) {
    const $timer = $newPhase.children('.timer');
    if ($timer.exists()) {
        // send to subscribers signal to change view
        console.log(getTimerDurationInSeconds($timer.text()));
        stompClient.send('/app/' + gameSessionId + '/changePhase', {}, JSON.stringify({
            'phaseNumber': phaseOrder,
            'timerDuration': getTimerDurationInSeconds($timer.text())
        }));
        startTimer($timer, () => onTimerFinish($timer, $newPhase));
    } else {
        stompClient.send('/app/' + gameSessionId + '/changePhase', {}, JSON.stringify({
            'phaseNumber': phaseOrder,
        }));
    }
}

/**
 * Function notifies all subscribers that next round starts, changes color of rounds.
 * If current round if the last one, finishes game.
 * */
function nextRound() {
    // notify subscribers that next round starts
    stompClient.send('/app/' + gameSessionId + '/nextRound');

    // clear timers if any
    clearTimers();
    setTimersOriginalValues();

    // hide player-table
    togglePlayersView();

    // set current-score-td to zero
    $('.current-score-td').text(0);

    // erase all sent answers
    $('.answer-td').text('');

    // erase calculated places
    $('.place-td').text('');

    // remove received-answers class from all commands' tr
    $('.players-table tbody tr').removeClass('received-answers');

    // remove all phases' classes except phase
    $('.phase').attr('class', 'phase animated');

    // set first phase active and second next
    $('#phase-0').addClass('active');
    $('#phase-1').addClass('next');

    // set active round to played
    $('.round.active').removeClass('active').addClass('played');

    const $newActive = $('.round.next');
    // if next round does not exist finish game
    if (!$newActive.exists()) finishGame();
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
            $('#phase-6').find('.phase-name').text('Завершить игру')
        }
        stompClient.send('/app/' + gameSessionId + '/changeRound', {}, getRoundOrder(roundId));
    }
}

function finishGame() {
    stompClient.send('/topic/' + gameSessionId + '/finishGame', {}, '');
}


/********************************
 *       POP-UP REACTIONS       *
 ********************************/

$('.confirmation-popup').click(() => {

});

$('.early-game-finish-btn').click(function () {
    $('.confirmation-popup').hide();
    finishGame();
});

$('.early-round-finish-btn').click(function () {
    $('.confirmation-popup').hide();
    nextRound();
});

$('.pass-round-popup-btn').click(function () {
    $('.confirmation-popup').hide();
    nextRound();
});

$('.previous-phase-popup-btn').click(function () {
    $('.confirmation-popup').hide();
    previousPhase();
});


/********************************
 *        TIMER FUNCTIONS       *
 ********************************/

/**
 * Persists timer start time, subscribes to timer.
* */
function startTimer($timer, onFinishedCallback) {
    timerId = window.setInterval(() => {
        decreaseTimer($timer, onFinishedCallback);
    }, 1000);
    timerSubscription = stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
        $timer.text(message.body);
    }, {});
    stompClient.send('/app/' + gameSessionId + '/startTimer', {}, $timer.text())
}

function decreaseTimer($timer, onFinishedCallback) {
    const timeStr = $timer.text();
    const dividerInd = timeStr.indexOf(':');
    let min = parseInt(timeStr.slice(0, dividerInd));
    let sec = parseInt(timeStr.substr(dividerInd + 1));
    if (min === 0 && sec === 0) {
        // stop timer
        window.clearInterval(timerId);

        onFinishedCallback();
        return;
    }
    if (min === 0 && sec < 12) {
        $timer.css('color', 'tomato');
    }
    if (min > 0 && sec === 0) {
        min--;
        sec = 60;
    }
    sec = --sec < 10 ? '0' + sec : sec;
    min = min < 10 ? '0' + min : min;
    stompClient.send('/topic/' + gameSessionId + '/timer', {}, `${min}:${sec}`);
}

function clearTimers() {
    window.clearInterval(timerId);
    const $timer = $('.timer');
    $timer.removeAttr('style');
    $timer.show();
    $('.add-30-sec-btn').hide();
    if (timerSubscription != null) timerSubscription.unsubscribe();
}

function getTimerDurationInSeconds(strDuration) {
    const delimiter = strDuration.indexOf(':');
    return parseInt(strDuration.slice(0, delimiter)) * 60   // minutes
        + parseInt(strDuration.substr(delimiter + 1));      // seconds
}

function onTimerFinish($timer, $phase) {
    $timer.hide();
    $phase.children('.add-30-sec-btn').show();
    timerSubscription.unsubscribe();
}

/**
 * Returns timers to its original. Is needed when next round is started.
 * */
function setTimersOriginalValues() {
    $('.timer').each(function () {
        $(this).text($(this).parent().children('.timer-original-value').text());
    })
}

/********************************
 *         PLAYER LOGOUT        *
 ********************************/

// show logout button for connected players on hover
$('.player').hover(function (event) {
    const player = event.currentTarget;
    if ($(player).hasClass('connected')) {
        $('#' + player.id + '-logout').css('display', 'block');
        $('#' + player.id + '-command-name').hide();
    }
}, function (event) {
    const player = event.currentTarget;
    if ($(player).hasClass('connected')) {
        showCommandName(player.id);
    }
});

// on click on logout btn logout player and push him to an index page
$('.player-logout-svg').click(function (event) {
    const id = event.currentTarget.id;
    const playerId = id.slice(0, id.indexOf('-logout'));
    stompClient.send(`/app/logout/${playerId}`);
});

// shows players's command name and hides logout btn
function showCommandName(playerId) {
    $('#' + playerId + '-logout').hide();
    $('#' + playerId + '-command-name').show();
}

/********************************
 *      ON CLICK FUNCTIONS      *
 ********************************/

$('.round').click(function (event) {
    const stage = event.currentTarget;
    if ($(stage).hasClass('next')) {
        // check if next phase is last. if not show confirmation popup
        if (getPhaseOrder($('.phase.next').attr('id')) === $('.phase').length - 1) {
            nextRound();
        } else {
            console.log('entered');
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
        console.log('Entered');
        $('#early-game-finish-popup').show();
    } else {
        // if current round is last check if current phase if last
        if ($('.phase.next').text() === 'Завершить игру') {
            finishGame();
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
    startTimer($timer, () => onTimerFinish($timer, $phase));

    // if parent of this btn is send-responses phase inform subscribers about additional time.
    if ($phase.attr('id') === 'phase-3') {
        stompClient.send('/topic/' + gameSessionId + '/additionalAnswerSendTime');
    }
});

$('.phase').click(function (event) {
    const phase = event.currentTarget;
    if ($(phase).hasClass('next')) {
        nextPhase();
    } else if ($(phase).hasClass('previous')) {
        $('#previous-phase-popup-' + getPhaseOrder(phase.id)).show();
    }
});

/********************************
 *       HELPER FUNCTIONS       *
 ********************************/

function getPhaseOrder(phaseId) {
    return parseInt(phaseId.substr(phaseId.indexOf('-') + 1));
}

function getRoundOrder(stageId) {
    return parseInt(stageId.substr(stageId.indexOf('-') + 1));
}

// helper function to check if jQuery returns an element
$.fn.exists = function () {
    return this.length !== 0;
};

function togglePlayersView() {
    $('.players').toggle();
    $('.players-table').toggle();
}

/********************************
 *          ON PAGE LOAD        *
 ********************************/

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
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
                showCommandName(playerId);
                break;
            case 'Disconnected':
                player.removeClass('connected').addClass('disconnected');
                playerRow.removeClass('connected').addClass('disconnected');
                showCommandName(playerId);
                break;
        }
    });
    stompClient.subscribe('/queue/' + gameSessionId + '/answer', (message) => {
        const body = JSON.parse(message.body);
        const username = body.username;
        const answerStr = body.answerStr;
        const newScore = parseInt(body.score);
        const $playerRow = $('#player-row-' + username);
        const $currentScoreTd = $playerRow.children('.current-score-td');
        const $totalTd = $playerRow.children('.total-score-td');
        const currentScore = parseInt($currentScoreTd.text());

        // display received answer to user
        $playerRow.children('.answer-td').text(answerStr === '' ? '-' : answerStr);

        // change current score
        $currentScoreTd.text(newScore);

        // change accumulated score
        $totalTd.text(parseInt($totalTd.text()) - currentScore + newScore);

        // change style
        $playerRow.addClass('received-answers');

        // update table
        const $table = $('.players-table');
        $table.trigger('updateCell', $currentScoreTd[0]);
        $table.trigger('updateCell', $totalTd[0]);
        sortingByTotalResult = false;
        $table.trigger('sorton', [[[3,1]]]);
    });

    const $phase = $('.phase.active');
    // if page is reloaded and timer was running proceed running
    if ($('.timer-is-running').exists()) {
        startTimer($phase.children('.timer'), () => onTimerFinish($phase.children('.timer'), $phase));
    }
    // if current phase has timer and page was reloaded after timer finished, show +30 sec btn
    if ($('.timer-finished').exists()) {
        $phase.children('.timer').hide();
        $phase.children('.add-30-sec-btn').show();
    }
    // if current phase order is greater than 2, show players-table
    if (getPhaseOrder($phase.attr('id')) > 2) togglePlayersView();
});

/********************************
 *         TABLE SORTING        *
 ********************************/

let sortingByTotalResult = true;
$('th').click((event) => {
    const heading = event.currentTarget;
    sortingByTotalResult = $(heading).text() === 'Накопленный результат';
});

/*// add parser through the tablesorter addParser method
$.tablesorter.addParser({
    // set a unique id
    id: 'scores',
    is: function(s) {
        // return false so this parser is not auto detected
        return false;
    },
    format: function(s) {
        // format your data for normalization
        return parseInt(s);
    },
    // set type, either numeric or text
    type: 'numeric'
});*/

$(document).ready(() => {
    const $table = $('.players-table');
    $table.tablesorter({cssHeader:'', sortList: [[4,1]], headers: {0 : {sorter:'integer'}, 3 : {sorter:'integer'}, 4: {sorter:'integer'}}});
    $table.bind('sortEnd', () => {
        if (sortingByTotalResult) {
            computePlaces();
        }
        console.log('sorted places');
    });
    computePlaces();
});

/**
 * Helper function to calculate current places of connected commands.
 * Computes places according to total-score column. 
* */
function computePlaces() {
    const $placeCells = $('.place-td');
    const $connectedCells = $('.connection-td');
    const $answerCells = $('.answer-td');
    const $totalScoreCells = $('.total-score-td');
    const numberOfTeams = $placeCells.length;

    // if order of sort is reverse apply places in reverse order
    if (parseInt($($totalScoreCells[0]).text())
        < parseInt($($totalScoreCells[numberOfTeams - 1]).text())) {
        let numberOfPlaces = 0;
        // count number of places
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                numberOfPlaces++;
            }
        }
        // apply places in reverse order
        let place = numberOfPlaces;
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                $($placeCells[i]).text(place--);
            }
        }
    } else {
        let place = 1;
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                $($placeCells[i]).text(place++);
            }
        }
    }
}
