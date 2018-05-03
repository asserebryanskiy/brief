import $ from "jquery";

export default class GameSessionUtils {
    static getPhaseOrder($phase) {
        return parseInt($phase[0].className.substr($phase[0].className.indexOf('phase-') + 6, 1));
    }

    static getCurrentRoundIndex() {
        let roundIndex = 0;

        // try get from active round
        const $round = $('.round.active');
        if ($round.length) {
            parseInt($round.attr('id').substr(6))
        }

        return roundIndex;
    }

    static getGameSessionId() {
        const href = window.location.href;
        return href.substr(href.lastIndexOf('/') + 1);
    }

    static getGameId() {
        return parseInt($('#game-id').text());
    }

    static getPhases() {
        const phases = {};
        $('.phase').each((i, el) => {
            phases[$(el).find('.english-name').text()] = this.getPhaseOrder($(el));
        });
        return phases;
    }

    static getPlayerId() {
        return parseInt($('#player-id').text());;
    }
}