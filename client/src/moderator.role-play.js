import $ from 'jquery'
import SockJS from "sockjs-client"
import Stomp from "@stomp/stompjs"
import RolePlayController from "./role-play-controller";
import GameSessionUtils from "./game-session-utils";
import WsService from "./ws-service";

/*
* Если текущий раунд не 0, то убираем фазы "Покдлючение участников" и "Распределение ролей"
*
* При завершении раунда и клике "Смена ролей" первая фаза называется "Инструкция",
* участникам отправляется объет ChangePhaseDto, а также новая роль и инструкция.
*
* При завершении раунда и клике "Следующий доктор" у медпредов перейдите туда-то,
* у докторов "Ожидайте следующего мед-преда".
* Соответственно первая фаза называется "Переход", вторая "Инструкция"
*
* */

let phases = GameSessionUtils.getPhases();
const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'rolePlay');
const controller = new RolePlayController(wsService, phases);
let roundIndex = GameSessionUtils.getCurrentRoundIndex();

function onWsConnect(stompClient) {

}

controller.connect(onWsConnect);

$('.phase').click((event) => {
    const $phase = $(event.currentTarget);
    if ($phase.hasClass('next') || $phase.hasClass('previous')) {
        const phaseIndex = GameSessionUtils.getPhaseOrder($phase);

        controller.changePhase(phaseIndex);
    }
});

$('.results-fork-phase').click((event) => {
    const $phase = $(event.currentTarget);
    if ($phase.hasClass('next')) {
        $phase.addClass('active').removeClass('next');
        $('.phase-' + phases["SURVEY"]).removeClass('active').addClass('previous');
        wsService.sendToGame('sendResults', '');
    }
});

$('.change-roles-fork-phase').add('.next-doctor-fork-phase').click((event) => {
    const $phase = $(event.currentTarget);
    if ($phase.hasClass('next')) {
        let instruction = '';
        if ($phase.hasClass('change-roles-fork-phase')) instruction = 'changeRoles';
        else instruction = 'nextDoctor';
        console.log(instruction);
        roundIndex++;
        controller.nextRound(instruction, roundIndex);
    }
});

