import $ from 'jquery';
import GameSessionController from "../game-session-controller";
import Timer from "../timer";
import GameSessionUtils from "../game-session-utils";
import BriefUtils from "./brief-utils";

export default class BriefModeratorController extends GameSessionController {
    constructor(wsService) {
        super(wsService);
        this.timer = new Timer(wsService.stompClient);
        this.phases = GameSessionUtils.getPhases();
    }

    changePhase(phaseIndex) {
        // clear timers if any
        this.timer.clearTimers();

        // if newActive order is greater than 2 show player-table
        const $playersTable = $('.players-table');
        if ((phaseIndex >= this.phases["SEND_ANSWERS"] && !$playersTable.is(':visible'))
            || (phaseIndex < this.phases["SEND_ANSWERS"] && $playersTable.is(':visible')))
            this.togglePlayersView();

        // if new phase is SEND_STATISTICS send statistics to players and projector
        if (phaseIndex === this.phases["RESULTS"]) {
            this.wsService.sendToApp('sendStatistics', '');
        }

        /// remove classes from all phases
        $('.phase').removeClass('active next previous');

        // add next, active, previous classes
        $('.phase-' + phaseIndex).removeClass('played').addClass('active');
        $('.phase-' + (phaseIndex + 1)).addClass('next');
        $('.phase-' + (phaseIndex - 1)).addClass('played previous');

        // timer will start on the server
        this.wsService.sendToApp('changePhase', phaseIndex);
    }

    nextRound() {
        // clear timers if any
        this.timer.clearTimers();
        BriefUtils.setTimersOriginalValues();

        // hide player-table
        if ($('.players-table').is(':visible')) {
            this.togglePlayersView();
        }

        // set current-score-td to zero
        $('.current-score-td').text(0);

        // erase all sent answers
        $('.answer-td').text('');

        // erase calculated places
        $('.place-td').text('');

        // remove received-answers class from all commands' tr
        $('.players-table tbody tr').removeClass('received-answers');

        // set active round to played
        $('.round.active').removeClass('active').addClass('played');

        const $newActive = $('.round.next');
        // if next round does not exist finish game
        if (!$newActive.length) this.finishGame();
        else {
            // set next round to active
            $newActive.removeClass('next').addClass('active');

            // set next to new active next
            const roundIndex = BriefUtils.getRoundOrder($newActive);
            const $nextRound = $('#round-' + (roundIndex + 1));
            $nextRound.addClass('next');

            // send to server
            this.wsService.sendToApp('changeRound', roundIndex);
        }

        $('.phase').removeClass('previous played');

        this.changePhase(0);
    }

    togglePlayersView() {
        $('.players').toggle();
        $('.players-table').toggle();
    }

    finishGame() {
        // ToDo: catch this in player.brief-controller
        this.wsService.sendToApp('finishGame', '');
    }
}