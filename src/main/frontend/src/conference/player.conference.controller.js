import $ from 'jquery'
import {PlayerUtils} from "../player.utils";
import {RiskMapService} from "./risk-map.service";
import {TimerService} from "./timer.service";
import * as M from "../../vendor/materialize";
import {InstantMessageService} from "./instant-message.service";

export class PlayerConferenceController {
    constructor() {

    }

    changePhaseByName(phaseName) {
        $('.phase-container').removeClass('active');
        const phaseCssId = '#' + PlayerUtils.getCssClassFromConstant(phaseName) + '-phase';
        console.log(phaseCssId);
        $(phaseCssId).addClass('active');

        window.scrollTo(0,0);
    }

    handleTimerMessageReceived(message) {
        let callback = () => { InstantMessageService.addInstantMessage('Время вышло.', 'failure') };
        TimerService.changeTimer(message, callback);
    }

    logout() {

    }
}