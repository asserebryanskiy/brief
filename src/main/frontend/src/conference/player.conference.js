import $ from 'jquery'
import WsService from "../ws-service";
import GameSessionUtils from "../game-session-utils";
import {PlayerConferenceController} from "./player.conference.controller";
import * as M from "../../vendor/materialize";
import {RiskMapService} from "./risk-map.service";
import {BestPracticesComponent} from "./best-practices.component";
import {GreetingComponent} from "./greeting/greeting.component";
import {SelfAnalysisComponent} from "./self-analysis/self-analysis.component";
import Swiper from "swiper";

const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'conference');
const bestPracticesComponent = new BestPracticesComponent(wsService);
const riskMapService = new RiskMapService();

const controller = new PlayerConferenceController();
function onWsConnect() {

    wsService.subscribe('/topic/conference/' + gameId + '/changePhase', (message) => {
        controller.changePhaseByName(JSON.parse(message.body).phaseName);
    });
    wsService.subscribe('/topic/game/' + gameId + '/timer', (message) => {
        controller.handleTimerMessageReceived(message);
    });

}

wsService.connect(onWsConnect);
// risk-map actions
$('.small-img-wrapper').click((event) => RiskMapService.handleSmallImgClicked(event));
$('.answer-input').click((event) => riskMapService.handleAnswerInputClicked(event));
$('.risk-map-send-responses-btn').click(event => riskMapService.handleSendResponsesClicked(event));

// best practices
$('.best-practices-send-btn').click(event => bestPracticesComponent.handleSendBtnClicked(event));
$('.best-practices-change-btn').click(event => bestPracticesComponent.handleChangeBtnClicked(event));
$('.cancel-best-practice-change-btn').click(event => BestPracticesComponent.cancelBpChange(event));

// self analysis
$('.self-analysis-send-btn').click(event => SelfAnalysisComponent.handleSendAnswersClicked());
$('.change-self-analysis-btn').click(event => SelfAnalysisComponent.handleChangeAnswersClicked(event));
$('#self-analysis-download-pdf-btn').click(event => SelfAnalysisComponent.getPdf());

// logout
$('.confirm-logout-btn').click(event => controller.handleLogout());

// materialize initialization
$(document).ready(event => {
    var modals = document.querySelectorAll('.modal');
    M.Modal.init(modals);

    const swiper = new Swiper('.swiper-container', {
        loop: true,
        autoHeight: true,
        initialSlide: 1,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
        pagination: {
            el: '.swiper-pagination',
            dynamicBullets: true
        },
    });

    new GreetingComponent(swiper);
    new SelfAnalysisComponent();

    $('.preloader').hide();
    // var carousels = document.querySelectorAll('.carousel');
    // var dropdowns = document.querySelectorAll('.dropdown-trigger');
    // M.Dropdown.init(dropdowns);
});

// on scroll fix timer
$(window).scroll(event => {
    if ($(window).scrollTop() > 35) {
        $('.timer').addClass('fixed-to-top');
    } else {
        $('.timer').removeClass('fixed-to-top');
    }
});