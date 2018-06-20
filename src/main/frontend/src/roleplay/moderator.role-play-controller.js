import $ from "jquery";
import GameSessionUtils from "../game-session-utils";
import TimerUtils from "./TimerUtils";

export default class RolePlayController {
    constructor(wsService, phases) {
        this.wsService = wsService;
        this.phases = phases;
    }

    static changePhase(phaseIndex) {
        // remove classes from all phases
        $('.phase').removeClass('active next previous played');

        // remove ready indicator from all players
        $('.player').removeClass('ready');

        // add next, active, previous and played classes
        for (let i = 0; i < phaseIndex + 2; i++) {
            const $phase = $('.phase-' + i);
            if (i === phaseIndex - 1)  $phase.addClass('previous');
            else if (i < phaseIndex)   $phase.addClass('played');
            else if (i === phaseIndex) $phase.addClass('active');
            else                       $phase.addClass('next');
        }

        // hide add-30-sec buttons and show timers
        $('.add-30-sec-btn').hide();
        $('.timer').show();
    }

    handlePhaseClick(event) {
        const $phase = $(event.currentTarget);
        if ($phase.hasClass('next') || $phase.hasClass('previous')) {
            // if phase is SEND_ROLES prevent starting new phase if number of players is odd
            if (GameSessionUtils.getPhaseOrder($phase) === this.phases["SEND_ROLES"]
                && $('.player').not('.player-template').length % 2 !== 0) {
                $('.odd-number-of-players-popup').show();
                return;
            }

            this.wsService.sendToApp('changePhase', GameSessionUtils.getPhaseOrder($phase));
        }
    }

    static handleTimerMessageReceived(message) {
        const $timer = $('.phase.active .timer');
        const $30secBtn = $('.phase.active .add-30-sec-btn');
        if (!$timer.is(':visible')) {
            $timer.show();
            $30secBtn.hide();
        }

        // parse incoming data
        let sec = TimerUtils.getSeconds(message.body);
        let min = TimerUtils.getMinutes(message.body);

        // check if timer ended and if true react
        if (min === 0 && sec === 0) {
            $timer.hide();
            $30secBtn.show();
        }

        // change timer text
        $timer.text(TimerUtils.convertToTimerString(min, sec));
    }

    handle30secBtnClick(event) {
        if ($(event.currentTarget).parents('.phase').hasClass('active')) {
            this.wsService.sendToApp('add30sec', '');
        }
    }

    handleLogoutPlayer(event) {
        const id = event.currentTarget.id;
        console.log(id);
        const playerId = id.slice(0, id.indexOf('-logout'));
        this.wsService.sendToApp('logoutPlayer', playerId);
    }

    static handlePlayerIsReady(message) {
        const playerId = message.body;
        $('#player' + playerId).addClass('ready');
    }
}