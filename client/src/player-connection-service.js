import $ from "jquery";
import GameSessionUtils from "./game-session-utils";

export default class PlayerConnectionService {
    constructor(gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    subscribe(wsService) {
        wsService.subscribe('/queue/' + this.gameSessionId + '/connection', function (message) {
            const json = JSON.parse(message.body);
            const instruction = json['instruction'];
            const username = json['username'];
            // some text that uniquely identifies player for moderator (command name/id)
            // username is not used because it is ugly and English
            const identifierForModerator = json['identifierForModerator'];

            switch (instruction) {
                /*case 'LOGIN':
                    addLoggedInPlayer();
                    break;*/
                case 'CONNECT':
                    PlayerConnectionService.indicateConnection(username, identifierForModerator);
                    break;
                case 'DISCONNECT':
                    PlayerConnectionService.indicateDisconnection(username);
                    break;
                case 'LOGOUT':
                    PlayerConnectionService.removeLoggedOutPlayer();
                    break;
            }
        });
    }

    static indicateDisconnection(username) {
        const $player = $('#' + username);
        const $playerRow = $('#player-row-' + username);

        this.signalDisconnection($player);
        this.signalDisconnection($playerRow);

        $playerRow.children('.connection-td').text('Disconnected');
        $playerRow.removeClass('connected').addClass('disconnected');
    }

    static removeLoggedOutPlayer($player, $playerRow) {
        $player.removeClass('connected');
        $playerRow.removeClass('connected-row');
        $playerRow.children('.connection-td').text('Disconnected');
    }

    static indicateConnection(username, identifierForModerator) {
        // noinspection JSJQueryEfficiency
        let $player = $('#' + username);

        // if player has not been added to screen yet, add it!
        if (!$player.length) {
            this.addPlayer(username, identifierForModerator);
        } else {
            // noinspection JSJQueryEfficiency
            $player = $('#' + username);
        }

        let $playerRow = $('#player-row-' + username);

        $player.removeClass('disconnected').addClass('connected');
        $playerRow.removeClass('disconnected').addClass('connected-row');
        $playerRow.children('.connection-td').text('Connected');
        return {$player, $playerRow};
    }

    static addPlayer(username, identifierForModerator) {
        // clone template for player-row and player
        const $playerTemplate = $('.player-template');
        const $playerRowTemplate = $('.player-row-template');
        const $player = $playerTemplate.clone();
        const $playerRow = $playerRowTemplate.clone();
        const $both = $player.add($playerRow);

        // set player's id and player's row id
        $player.attr('id', username);
        $playerRow.attr('id', 'player-row-' + username);

        // set player's identifier for moderator
        $both.find('.command-name').text(identifierForModerator);

        // set player's logout svg id (in table row also)
        $player.find('svg').attr('id', username + '-logout');
        $playerRow.find('svg').attr('id', username + '-logout-table');

        // add player to screen after template
        $player.insertAfter($playerTemplate);
        $playerRow.insertAfter($playerRowTemplate);

        // remove player's classes hidden and player-template
        $both.removeClass('player-template player-row-template hidden');
    }

    static signalDisconnection($el) {
        $el.removeClass('connected connected-row').addClass('disconnected');
        window.setTimeout(() => $el.removeClass('disconnected'), 500);
        window.setTimeout(() => $el.addClass('disconnected'), 1000);
        window.setTimeout(() => $el.removeClass('disconnected'), 1500);
    }
}