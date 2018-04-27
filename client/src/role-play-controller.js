import $ from "jquery";
import SockJS from "sockjs-client"
import Stomp from "@stomp/stompjs"
import GameSessionController from './game-session-controller'

export default class RolePlayController extends GameSessionController {
    constructor(wsService, phases) {
        super(wsService);
        this.wsService = wsService;
        this.phases = phases;
    }

    changePhase(phaseIndex) {
        console.log(phaseIndex);
        // if phase is SEND_ROLES prevent starting new phase if number of players is odd
        if (phaseIndex === this.phases["SEND_ROLES"] && $('.player').not('.player-template').length % 2 !== 0) {
            $('.odd-number-of-players-popup').show();
            return;
        }

        // remove classes from all phases
        $('.phase').add('.fork-phase').removeClass('active next previous');

        // add next, active, previous classes
        $('.phase-' + phaseIndex).removeClass('played').addClass('active');
        $('.phase-' + (phaseIndex + 1)).addClass('next');
        $('.phase-' + (phaseIndex - 1)).addClass('played previous');

        // timer will start from the server
        // save phase change on the server
        this.wsService.sendToGame('changePhase', phaseIndex);

        // do phase specific stuff
        switch (phaseIndex) {
            case this.phases["SURVEY"]:
                $('.fork-phase').removeClass('inaccessible').addClass('next');
                break;
        }
    }

    nextRound(instruction) {
        const $phase0 = $('.phase-0');
        const $crossingPhase = $('.phase-' + this.phases["CROSSING"]);
        if ($phase0.length) {
            $phase0.add('.phase-1').add('.phase-2').parents('.phase-wrapper').remove();

            // change order of phases
            const instrInd = this.phases["SEND_INSTRUCTION"];
            const crossInd = this.phases["CROSSING"];
            const $instructionsPhase = $('.phase-' + instrInd);
            $crossingPhase.parents('.phase-wrapper').insertBefore(
                $instructionsPhase.parents('.phase-wrapper'));
            $crossingPhase.removeClass('phase-' + crossInd).addClass('phase-' + instrInd);
            $instructionsPhase.removeClass('phase-' + instrInd).addClass('phase-' + crossInd);
            this.phases["SEND_INSTRUCTION"] = crossInd;
            this.phases["CROSSING"] = instrInd;
        }

        $('.phase').removeClass('previous played');
        $('.fork-phase').removeClass('next').addClass('inaccessible');

        switch (instruction) {
            case 'changeRoles':
                $crossingPhase.hide();
                this.changePhase(this.phases["SEND_INSTRUCTION"]);
                break;
            case 'nextDoctor':
                $crossingPhase.show();
                this.changePhase(this.phases["CROSSING"]);
                break;
        }

        this.wsService.sendToGame('nextRound', instruction);
    }
}