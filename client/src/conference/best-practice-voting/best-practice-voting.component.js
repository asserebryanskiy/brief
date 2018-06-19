import $ from 'jquery'
import {PlayerUtils} from "../../player.utils";
import GameSessionUtils from "../../game-session-utils";

export class BestPracticeVotingComponent {
    constructor() {
        this.playerId = PlayerUtils.getPlayerId();
        this.gameId = GameSessionUtils.getGameId();

        $.getJSON('/api/conference/bestPractice/' + this.gameId, (data) => {
            for (let practice of data) this.addBestPractice(practice);
        });
    }

    addBestPractice(practice) {
        const $practice = $('.best-practice-voting')
    }
}