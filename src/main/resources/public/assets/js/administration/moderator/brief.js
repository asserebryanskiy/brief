const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);

const href = window.location.href;
const gameSessionId = href.substr(href.lastIndexOf('/') + 1);
let timerId;

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/queue/' + gameSessionId + '/connection', function (message) {
        const mes = message.body;
        const divider = mes.indexOf(' ');
        const command = mes.slice(0, divider);
        const playerId = mes.substr(divider + 1);
        const player = $('#' + playerId);
        switch (command) {
            case 'Connect':
                player.removeClass('disconnected');
                player.addClass('connected');
                break;
            case 'Logout':
                player.removeClass('connected');
                showCommandName(playerId);
                break;
            case 'Disconnected':
                player.removeClass('connected');
                player.addClass('disconnected');
                showCommandName(playerId);
                break;
        }
    });
});

// shows players's command name and hides logout btn
function showCommandName(playerId) {
    $('#' + playerId + '-logout').hide();
    $('#' + playerId + '-command-name').show();
}

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
    const playerId = id.slice(0, id.lastIndexOf('-'));
    stompClient.send(`/app/logout/${playerId}`);
});

function clearTimers() {
    window.clearInterval(timerId);
    const $timer = $('.timer');
    $timer.removeAttr('style');
    $timer.show();
    $('.add-30-sec-btn').hide();
}

function nextPhase() {
    // send to subscribers signal to change view
    stompClient.send('/app/' + gameSessionId + '/nextPhase', {}, '');

    // clear timers if any
    clearTimers();

    // set active phase to played
    $('.phase.active').removeClass('active').addClass('played');

    // set this phase to active
    const $newActive = $('.phase.next');
    $newActive.removeClass('next').addClass('active');

    // set next to it phase to next
    const nextPhaseId = 'phase-' + (getPhaseOrder($newActive.attr('id')) + 1);
    const $nextPhase = $('#' + nextPhaseId);
    if ($nextPhase.exists()) {
        $nextPhase.addClass('next');
    } else {
        nextStage();
    }

    // establish timer if it exists
    const $timer = $newActive.children('.timer');
    if ($timer.exists()) {
        timerId = window.setInterval(() => {
            decreaseTimer($timer, $newActive);
        }, 1000)
    }
}

$('.add-30-sec-btn').click(function (event) {
    const btn = event.currentTarget;
    const $phase = $(btn).parent();
    const $timer = $phase.children('.timer');
    $(btn).hide();
    $timer.text('0:30');
    $timer.show();
    $timer.css('color', 'initial');
    timerId = window.setInterval(() => {
        decreaseTimer($timer, $phase);
    }, 1000);
});

$('.phase').click(function (event) {
    const phase = event.currentTarget;
    if ($(phase).hasClass('next')) {
        nextPhase();
    }
});

function setOriginalValue($timers) {
    $timers.each(function () {
        $(this).text($(this).parent().children('.timer-original-value').text());
    })

}

/*
* Function notifies all subscribers that next stage starts, changes color of stages.
* If current stage if the last one, finishes game.
* */
function nextStage() {
    // notify subscribers that next stage starts
    stompClient.send('/app/' + gameSessionId + '/nextStage');

    // clear timers if any
    clearTimers();
    setOriginalValue($('.timer'));

    // remove all phases' classes except phase
    $('.phase').attr('class', 'phase animated');

    // set first phase active and second next
    $('#phase-0').addClass('active');
    $('#phase-1').addClass('next');

    // set active stage to played
    $('.stage.active').removeClass('active').addClass('played');

    const $newActive = $('.stage.next');
    // if next stage does not exist finish game
    if (!$newActive.exists()) finishGame();
    else {
        // set next stage to active
        $newActive.removeClass('next').addClass('active');

        // set next to new active next
        const stageId = $newActive.attr('id');
        const nextStageId = 'stage-' + (getStageOrder(stageId) + 1);
        const $nextStage = $('#' + nextStageId);
        if ($nextStage.exists()) {
            $nextStage.addClass('next');
        } else {
            $('.phase.active .phase-name').text('Завершить игру')
        }
    }
}

// helper function to check if jQuery returns an element
$.fn.exists = function () {
    return this.length !== 0;
};

function finishGame() {

}

// function on click on game over btn
$('#game-over-btn').click(function () {
    // if current stage is not last
    if ($('.stage.next').exists()) {
        console.log('Entered');
        $('#early-game-finish-popup').show();
    } else {
        // if current stage is last check if current phase if last
        if ($('.phase.next').text() === 'Завершить игру') {
            finishGame();
        } else {
            $('#early-game-finish-popup').show();
        }
    }
});

function decreaseTimer($timer, $phase) {
    const timeStr = $timer.text();
    const dividerInd = timeStr.indexOf(':');
    let min = parseInt(timeStr.slice(0, dividerInd));
    let sec = parseInt(timeStr.substr(dividerInd + 1));
    if (min === 0 && sec === 1) {
        // stop timer
        window.clearInterval(timerId);

        // signal user that timer finished

        // change timer to +30 sec btn
        $timer.hide();
        $phase.children('.add-30-sec-btn').show();
        return;
    }
    if (min === 0 && sec === 11) {
        $timer.css('color', 'tomato');
    }
    if (min > 0 && sec === 0) {
        min--;
        sec = 60;
    }
    sec = --sec < 10 ? '0' + sec : sec;
    $timer.text(`${min}:${sec}`);
}

function getPhaseOrder(phaseId) {
    return parseInt(phaseId.substr(phaseId.indexOf('-') + 1));
}

function getStageOrder(stageId) {
    return parseInt(stageId.substr(stageId.indexOf('-') + 1));
}

$('.stage').click(function (event) {
    const stage = event.currentTarget;
    if ($(stage).hasClass('next')) {
        // check if next phase is last. if not show confirmation popup
        if (getPhaseOrder($('.phase.next').attr('id')) === $('.phase').length - 1) {
            nextStage();
        } else {
            console.log('entered');
            $('#early-round-finish-popup').show();
        }
    }
});

$('#pass-round-btn').click(function () {
    $('#pass-round-popup').show();
});


/*****************
* POP-UP REACTIONS
******************/
$('.early-game-finish-btn').click(function () {
    $('#early-game-finish-popup').hide();
    finishGame();
});

$('.early-round-finish-btn').click(function () {
    $('.confirmation-popup').hide();
    nextStage();
});

$('.pass-round-popup-btn').click(function () {
    $('.confirmation-popup').hide();
    nextStage();
});
