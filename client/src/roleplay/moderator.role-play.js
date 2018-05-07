import $ from 'jquery'
import RolePlayController from "./moderator.role-play-controller";
import GameSessionUtils from "../game-session-utils";
import WsService from "../ws-service";
import PlayerConnectionService from "../player-connection-service";

let phases = GameSessionUtils.getPhases();
const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'rolePlay');
const playerConnectionService = new PlayerConnectionService(GameSessionUtils.getGameSessionId());
const controller = new RolePlayController(wsService, phases);
// let roundIndex = GameSessionUtils.getCurrentRoundIndex();

function onWsConnect() {
    wsService.subscribe('/topic/game/' + gameId + '/changePhase', (message) => {
        RolePlayController.changePhase(phases[message.body]);
    });

    wsService.subscribe('/topic/game/' + gameId + '/timer', (message) => {
        RolePlayController.handleTimerMessageReceived(message);
    });

    playerConnectionService.subscribe(wsService);

    $('.preloader').fadeOut();
}

wsService.connect(onWsConnect);

$('.phase').click((event) => controller.handlePhaseClick(event));
$('.add-30-sec-btn').click((event) => controller.handle30secBtnClick(event));

