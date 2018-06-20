import WsService from "../../ws-service";
import GameSessionUtils from "../../game-session-utils";
import $ from "jquery";
import {ProjectorConferenceController} from "./projector.conference.controller";
import {ProjectorBestPracticeComponent} from "./projector-best-practice.component";

const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'conference');
const bestPracticeComponent = new ProjectorBestPracticeComponent();
const controller = new ProjectorConferenceController();

function onWsConnect() {
    $('.preloader').hide();

    wsService.subscribe('/topic/conference/' + gameId + '/changePhase', (message) => {
        controller.changePhaseByName(message.body);
    });

    wsService.subscribe('/queue/conference/' + gameId + '/bestPractice', (message) => {
        ProjectorBestPracticeComponent.addBestPractice(JSON.parse(message.body));
    });

    wsService.subscribe('/queue/conference/' + gameId + '/changeBestPractice', (message) => {
        ProjectorBestPracticeComponent.changeBestPractice(JSON.parse(message.body))
    });

    wsService.subscribe('/queue/conference/' + gameId + '/deleteBestPractice', (message) => {
        ProjectorBestPracticeComponent.delete(message.body)
    });
}

wsService.connect(onWsConnect);