import $ from 'jquery';
import WsService from "../ws-service";
import GameSessionUtils from "../game-session-utils";
import RolePlayController from "./game.role-play-controller";

const gameId = GameSessionUtils.getGameId();
const playerId = GameSessionUtils.getPlayerId();
const wsService = new WsService(gameId, 'rolePlay');
const controller = new RolePlayController(wsService);

function onWsConnect() {
    // subscribe to: instructions, phase change
    wsService.subscribe('/queue/rolePlay/player/' + playerId + '/instructions', (message) => {
        const json = JSON.parse(message.body);
        $('.role-name').text(json['roleName']);
        $('.instruction').text(json['instruction']);
    });

    wsService.subscribe('/queue/rolePlay/player/' + playerId + '/crossing', (message) => {
        const json = JSON.parse(message.body);
        $('.hospital-number').text(parseInt(json['hospital']) + 1);
        $('.room-number').text(parseInt(json['room']) + 1);
    });

    wsService.subscribe('/topic/game/' + gameId + '/changePhase', (message) => {
        controller.changePhaseByName(message.body);
    });

    wsService.subscribe('/queue/rolePlay/player/' + playerId + '/changePhase', (message) => {
        controller.changePhaseByName(message.body);
    });

    // subscribe on results

    $('.preloader').fadeOut();
}

// show only active phase
$('.phase-container').hide();
$('.phase-container.active').show();

$('.logout-text').click(() => {
    $('.logout-popup').show();
});

wsService.connect(onWsConnect);