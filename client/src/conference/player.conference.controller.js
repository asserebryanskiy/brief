import $ from 'jquery'
import {PlayerUtils} from "../player.utils";
import {RiskMapService} from "./risk-map.service";
import {TimerService} from "./timer.service";
import * as M from "../../vendor/materialize";

export class PlayerConferenceController {
    constructor(riskMapService, bestPracticesService, selfAnalysisService, timerService) {
        this.riskMapService = riskMapService;
        this.bestPracticesService = bestPracticesService;
        this.selfAnalysisService = selfAnalysisService;
        this.timerService = timerService;
    }


    changePhaseByName(phaseName) {
        $('.phase-container').removeClass('active');
        const phaseCssId = '#' + PlayerUtils.getCssClassFromConstant(phaseName) + '-phase';
        console.log(phaseCssId);
        $(phaseCssId).addClass('active');

        switch (phaseName) {
            case 'RISK_MAP':
                this.riskMapService.enableAnswerSend();
                break;
            case 'RISK_MAP_RESULTS':
                RiskMapService.handleResultsPhase();
                break;
        }
    }

    handleTimerMessageReceived(message) {
        let callback = PlayerUtils.getActivePhaseName();
        switch (PlayerUtils.getActivePhaseName()) {
            case 'risk-map':
                callback = this.riskMapService.getOnTimerEndedCallback();
                break;
            case 'best-practices':
                callback = this.bestPracticesService.getOnTimerEndedCallback();
                break;
            case 'self-analysis':
                callback = this.selfAnalysisService.getOnTimerEndedCallback();
                break;
        }
        TimerService.changeTimer(message, callback);
    }

    static handleGreetingCarouselTriggerClicked(event) {
        const imgNumber = $(event.target).attr('href').slice(9);
        const $greeting = $('#greeting-phase .carousel');
        $greeting.removeClass('hide');
        console.log($greeting);
        console.log($greeting[0]);
        M.Carousel.getInstance(document.getElementById('greeting-carousel')).set(imgNumber);
    }

    logout() {

    }
}