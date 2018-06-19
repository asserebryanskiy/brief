import $ from 'jquery'
import WsService from "../ws-service";
import GameSessionUtils from "../game-session-utils";
import PlayerConnectionService from "../player-connection-service";
import {ConferenceController} from "./moderator.conference.controller";

const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'conference');
const phases = GameSessionUtils.getPhases();
const controller = new ConferenceController(wsService, phases);
const playerConnectionService = new PlayerConnectionService(GameSessionUtils.getGameSessionId(), controller);

function onWsConnect() {
    wsService.subscribe('/topic/conference/' + gameId + '/changePhase', (message) => {
        ConferenceController.changePhase(phases[message.body]);
    });

    wsService.subscribe('/topic/game/' + gameId + '/timer', (message) => {
        ConferenceController.handleTimerMessageReceived(message);
    });

    wsService.subscribe('/topic/conference/' + gameId + '/playerIsReady', (message) => {
        ConferenceController.handlePlayerIsReady(message);
    });

    wsService.subscribe('/queue/conference/' + gameId + '/bestPractice', (message) => {
        console.log('recieved best practice: ' + message.body);
    });

    playerConnectionService.subscribe(wsService);

    $('.preloader').fadeOut();
}

wsService.connect(onWsConnect);

$('.phase').click((event) => controller.handlePhaseClick(event));