import $ from "jquery";
import SockJS from "sockjs-client"
import Stomp from "@stomp/stompjs"
import GameSessionController from '../game-session-controller'
import GameSessionUtils from "../game-session-utils";

export default class RolePlayController {
    constructor(wsService, phases) {
        this.wsService = wsService;
        this.phases = phases;
    }

    static changePhase(phaseIndex) {
        // remove classes from all phases
        $('.phase').removeClass('active next previous played');

        // add next, active, previous and played classes
        for (let i = 0; i < phaseIndex + 2; i++) {
            const $phase = $('.phase-' + i);
            if (i === phaseIndex - 1)  $phase.addClass('previous');
            else if (i < phaseIndex)   $phase.addClass('played');
            else if (i === phaseIndex) $phase.addClass('active');
            else                       $phase.addClass('next');
        }

        // timer will start from the server
    }

    handlePhaseClick(event) {
        const $phase = $(event.currentTarget);
        if ($phase.hasClass('next') || $phase.hasClass('previous')) {
            // if phase is SEND_ROLES prevent starting new phase if number of players is odd
            console.log($phase);
            console.log(this.phases);
            if (GameSessionUtils.getPhaseOrder($phase) === this.phases["SEND_ROLES"]
                && $('.player').not('.player-template').length % 2 !== 0) {
                $('.odd-number-of-players-popup').show();
                return;
            }

            this.wsService.sendToApp('changePhase', GameSessionUtils.getPhaseOrder($phase));
        }
    }
}