import $ from "jquery";
import TimerUtils from "../roleplay/TimerUtils";
import GameSessionUtils from "../game-session-utils";

export class ConferenceController {
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

            this.wsService.sendToApp('changePhase', GameSessionUtils.getPhaseOrder($phase));
        }
    }

    static handlePlayerIsReady(message) {
        const playerId = message.body;
        $('#player' + playerId).addClass('ready');
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
}