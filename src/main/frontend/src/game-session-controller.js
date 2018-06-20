import $ from 'jquery'
import WsService from './ws-service'
import GameSessionUtils from "./game-session-utils";

export default class GameSessionController {
    constructor(wsService) {
        this.wsService = wsService;
    }

    connect(onWsConnect) {
        let wsService = this.wsService;
        const gameSessionId = GameSessionUtils.getGameSessionId();
        function onConnection() {
            wsService.subscribe('/queue/' + gameSessionId + '/connection', function (message) {
                const json = JSON.parse(message.body);
                const instruction = json['instruction'];
                const username = json['username'];
                // some text that uniquely identifies player for moderator (command name/id)
                // username is not used because it is ugly and English
                const identifierForModerator = json['identifierForModerator'];
                let $player = $('#' + username);
                let $playerRow = $('#player-row-' + username);

                function signalDisconnection($el) {
                    $el.removeClass('connected connected-row').addClass('disconnected');
                    window.setTimeout(() => $el.removeClass('disconnected'), 500);
                    window.setTimeout(() => $el.addClass('disconnected'), 1000);
                    window.setTimeout(() => $el.removeClass('disconnected'), 1500);
                }

                /*function addLoggedInPlayer() {

                }*/

                function indicateConnection() {
                    // if player has not been added to screen yet, add it!
                    if (!$player.length) {
                        // clone template for player-row and player
                        const $playerTemplate = $('.player-template');
                        const $playerRowTemplate = $('.player-row-template');
                        $player = $playerTemplate.clone();
                        $playerRow = $playerRowTemplate.clone();
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
                    $player.removeClass('disconnected').addClass('connected');
                    $playerRow.removeClass('disconnected').addClass('connected-row');
                    $playerRow.children('.connection-td').text('Connected');
                }

                function removeLoggedOutPlayer() {
                    $player.removeClass('connected');
                    $playerRow.removeClass('connected-row');
                    $playerRow.children('.connection-td').text('Disconnected');
                }

                function indicateDisconnection() {
                    signalDisconnection($player);
                    signalDisconnection($playerRow);
                    $playerRow.children('.connection-td').text('Disconnected');
                    // $playerRow.removeClass('connected').addClass('disconnected');
                }

                switch (instruction) {
                    /*case 'LOGIN':
                        addLoggedInPlayer();
                        break;*/
                    case 'CONNECT':
                        indicateConnection();
                        break;
                    case 'LOGOUT':
                        removeLoggedOutPlayer();
                        break;
                    case 'DISCONNECT':
                        indicateDisconnection();
                        break;
                }
            });

            // do game specific stuff
            if (onWsConnect !== null && typeof onWsConnect !== 'undefined') onWsConnect(wsService);

            // show screen
            $('.preloader').fadeOut();
        }

        this.wsService.connect(() => onConnection());
        // this.stompClient.connect({}, () => onConnection(), () => reconnect());
    };
}