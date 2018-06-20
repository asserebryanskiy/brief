import $ from 'jquery';
import WsService from "../ws-service";
import GameSessionUtils from "../game-session-utils";
import RolePlayController from "./game.role-play-controller";
import * as M from "../../vendor/materialize";

const gameId = GameSessionUtils.getGameId();
const playerId = GameSessionUtils.getPlayerId();
const wsService = new WsService(gameId, 'rolePlay');
const controller = new RolePlayController(wsService);

function onWsConnect() {
    // subscribe to: instructions, phase change
    const playerQueue = '/queue/rolePlay/player/' + playerId;
    wsService.subscribe(playerQueue + '/instructions', (message) => {
        RolePlayController.handleInstructionMessageReceived(message);
    });

    wsService.subscribe(playerQueue + '/crossing', (message) => {
        const json = JSON.parse(message.body);
        $('.hospital-number').text(parseInt(json['hospital']) + 1);
        $('.room-number').text(parseInt(json['room']) + 1);
    });

    wsService.subscribe('/topic/game/' + gameId + '/changePhase', (message) => {
        RolePlayController.changePhaseByName(message.body);
    });

    wsService.subscribe(playerQueue + '/changePhase', (message) => {
        RolePlayController.changePhaseByName(message.body);
    });

    wsService.subscribe(playerQueue + '/yourResults', (message) => {
        RolePlayController.handleSalesmanResultsReceived(message);
    });

    wsService.subscribe(playerQueue + '/averageResults', (message) => {
        RolePlayController.handleAverageAnswersReceived(message);
    });

    wsService.subscribe('/topic/game/' + gameId + '/timer', (message) => {
        RolePlayController.handleTimerMessageReceived(message);
    });

    wsService.subscribe(playerQueue + '/logout', (message) => {
        RolePlayController.handleLogout();
    });

    wsService.subscribe('/queue/player/' + playerId + '/goToIndex', () => {
        window.location.href = location.protocol + '//' +  window.location.host;
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

$('.ee-answer-variant').click((event) => controller.handleEeAnswerVariantClick(event));
$('.drugs-distribution-help-btn').click(() => RolePlayController.handleOpenDrugDistributionHelpPopup());
$('.popup-back').click(() => $('.popup-wrapper').hide());
$('.close-popup-btn').click(() => $('.popup-wrapper').hide());
$('.drugs-distribution-input').on('input', (event) => controller.handleDrugsDistributionInputChange(event));
$('.ready-btn').click((event) => controller.handleReadyBtnClick(event));

// on answer-send
$('.doctor-send-responses-btn').click(() => controller.handleDoctorAnswerSend());
$('.salesman-send-responses-btn').click(() => controller.handleSalesmanAnswerSend());
$('.drugs-distribution-send-responses-btn').click(() => controller.handleDrugsDistributionSend());

// on scroll
$(window).scroll(() => RolePlayController.handleScroll());
$('.round-results-slider').scroll(() => RolePlayController.handleResultsSliderScroll());

wsService.connect(onWsConnect);

// forbid dash in inputs of number type
$('input[type="number"]').on('keydown', (event) => {
    console.log(event.keyCode);
    if (event.keyCode === 189) event.preventDefault();
});

// materialize initialization
document.addEventListener('DOMContentLoaded', function() {
    const modals = document.querySelectorAll('.modal');
    M.Modal.init(modals, {});

    const collapsible = document.querySelectorAll('.collapsible');
    M.Collapsible.init(collapsible, {
        accordion: false
    });
});