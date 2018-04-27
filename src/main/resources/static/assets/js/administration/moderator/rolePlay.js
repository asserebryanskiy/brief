import RolePlayController from './role-play-controller'

const controller2 = new RolePlayController();

const FORM_GAME_PHASE = 0;
const BEFORE_FORK_PHASE = 5;

let currentPhaseNumber = getActivePhaseNumber();
let gameId = $('#game-id').text();

switch (currentPhaseNumber) {
    case FORM_GAME_PHASE: {
        showGameSettings(true);
    }
}
controller.nextPhase = () => {
    // clear timers if any
    controller.timer.clearTimers();

    // set previous phase to played
    $('.phase.previous:visible').removeClass('previous').addClass('played');

    // set active phase to previous
    $('.phase.active:visible').removeClass('active').addClass('previous');

    // set this phase to active
    const $newActive = $('.phase.next:visible');
    $newActive.removeClass('next').addClass('active');

    // set next to it phase to next
    const phaseOrder = getPhaseOrder($newActive);
    console.log(phaseOrder);
    const $nextPhase = getPhaseNear($newActive, 1);
    if ($nextPhase.exists() && phaseOrder !== BEFORE_FORK_PHASE) {
        $nextPhase.addClass('next');
    } else {
        $('.fork-phase').find('.phase').removeClass('inaccessible').addClass('fork-phase-choice')
    }

    // do phase specific stuff
    switch (phaseOrder) {
        case FORM_GAME_PHASE + 1: {
            stompClient.send("/app/rolePlay/" + gameId + "/rolePlaySettings", {},
                JSON.stringify({
                    'strategy' : $('.role-play-strategy-option:selected').attr('value')
                }));
            showGameSettings(false);
            break;
        }
    }

    // establish timer if it exists
    controller.notifySubscribers($newActive, phaseOrder);
};
controller.nextRound = () => {
    // clear timers if any
    this.timer.clearTimers();
    setTimersOriginalValues();

    // send to subscribers change round instruction

    // remove all phases' classes except phase
    $('.phase').removeClass('active previous next played');

    // set first phase active and second next
    $('.phase-0').addClass('active');
    $('.phase-1').addClass('next');
};
controller.setOnPrevPhase((phaseOrder) => {
    switch (phaseOrder) {
        case FORM_GAME_PHASE:
            showGameSettings(true);
            break;
        case BEFORE_FORK_PHASE - 1:
            $('.fork-phase').find('.phase').addClass('inaccessible');
    }
});
controller.setOnNextRound(() => {

});
controller.connect();

function showGameSettings(value) {
    if (value) {
        $('.players-wrapper').hide();
        $('.form-game-wrapper').show();
    } else {
        $('.players-wrapper').show();
        $('.form-game-wrapper').hide();
    }
}

/***********************************************
 *                                             *
 *              FORK PHASE ACTIONS             *
 *                                             *
 ************************************************/

$('.results-fork-phase').click(() => {
    // remove next class phase from results-fork-phase and add active class
    $('.results-fork-phase').removeClass('next').addClass('active');

    // send statistics to subscribers
    stompClient.send('/app/rolePlay/' + gameId + '/sendStatistics')
});

$('.change-roles-fork-phase').click(() => {
    // remove from all fork-phases active and next class, add class inaccessible

    // send command to app to change roles

    // if round is 0 remove first 3 phases

    // start next round

});

