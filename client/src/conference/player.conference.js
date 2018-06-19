import $ from 'jquery'
import WsService from "../ws-service";
import GameSessionUtils from "../game-session-utils";
import {PlayerConferenceController} from "./player.conference.controller";
import * as M from "../../vendor/materialize";
import {RiskMapService} from "./risk-map.service";
import {BestPracticesComponent} from "./best-practices.component";
import {TimerService} from "./timer.service";
import {SelfAnalysisService} from "./self-analysis.service";
import {GreetingComponent} from "./greeting/greeting.component";
import {SelfAnalysisComponent} from "./self-analysis/self-analysis.component";

const gameId = GameSessionUtils.getGameId();
const wsService = new WsService(gameId, 'conference');
const riskMapService = new RiskMapService();
const bestPracticesComponent = new BestPracticesComponent(wsService);
const selfAnalysisService = new SelfAnalysisService();
const selfAnalysisComponent = new SelfAnalysisComponent();
const greetingComponent = new GreetingComponent();
const timerService = new TimerService();

const controller = new PlayerConferenceController(
    riskMapService,
    bestPracticesComponent,
    selfAnalysisService,
    timerService
);
function onWsConnect() {

    $('.preloader').hide();

    wsService.subscribe('/topic/conference/' + gameId + '/changePhase', (message) => {
        controller.changePhaseByName(message.body);
    });
    wsService.subscribe('/topic/game/' + gameId + '/timer', (message) => {
        controller.handleTimerMessageReceived(message);
    });

}

wsService.connect(onWsConnect);
// greeting actions
$('#greeting-phase .carousel-trigger').click(event => PlayerConferenceController.handleGreetingCarouselTriggerClicked(event));
$('.choose-greeting-img-btn').click(event => GreetingComponent.handleImageChosen(event));
$('.greeting-change-choice-btn').click(event => GreetingComponent.handleChangeChoice(event));
$('.greeting-gallery-popup-trigger').click(event => greetingComponent.openGalleryPopup(event));

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
$('.change-self-analysis-btn').click(event => SelfAnalysisComponent.handleChangeAnswersClicked());
$('#self-analysis-download-pdf-btn').click(event => SelfAnalysisComponent.getPdf());

// logout
$('.confirm-logout-btn').click(event => controller.handleLogout());

// materialize initialization
$(document).ready(event => {
    var modals = document.querySelectorAll('.modal');
    M.Modal.init(modals);
    var carousels = document.querySelectorAll('.carousel');
    M.Carousel.init(carousels);
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