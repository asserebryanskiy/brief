import $ from "jquery";
import GameSessionUtils from "./game-session-utils";

export default class PlayerConnectionService {
    constructor(gameSessionId, controller) {
        this.gameSessionId = gameSessionId;
        this.controller = controller;
    }

    subscribe(wsService) {
        const service = this;
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
                    service.indicateConnection(username, identifierForModerator);
                    break;
                case 'DISCONNECT':
                    service.indicateDisconnection(username);
                    break;
                case 'LOGOUT':
                    service.removeLoggedOutPlayer(username);
                    break;
            }
        });
    }

    indicateDisconnection(username) {
        const $player = $('#' + username);
        const $playerRow = $('#player-row-' + username);

        this.signalDisconnection($player);
        this.signalDisconnection($playerRow);

        $playerRow.children('.connection-td').text('Disconnected');
        $playerRow.removeClass('connected').addClass('disconnected');
    }

    removeLoggedOutPlayer(username) {
        $('#' + username).remove();
        $('#player-row-' + username).remove();
    }

    indicateConnection(username, identifierForModerator) {
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

    addPlayer(username, identifierForModerator) {
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
        $player.find('svg').attr('id', username + '-logout')
            .click((event) => this.controller.handleLogoutPlayer(event));
        $playerRow.find('svg').attr('id', username + '-logout-table')
            .click((event) => this.controller.handleLogoutPlayer(event));

        // add player to screen after template
        $player.insertAfter($playerTemplate);
        $playerRow.insertAfter($playerRowTemplate);

        // remove player's classes hidden and player-template
        $both.removeClass('player-template player-row-template hidden');
    }

    signalDisconnection($el) {
        $el.removeClass('connected connected-row').addClass('disconnected');
        window.setTimeout(() => $el.removeClass('disconnected'), 500);
        window.setTimeout(() => $el.addClass('disconnected'), 1000);
        window.setTimeout(() => $el.removeClass('disconnected'), 1500);
    }
}