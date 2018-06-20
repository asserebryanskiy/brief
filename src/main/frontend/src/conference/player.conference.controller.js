import $ from 'jquery'
import {PlayerUtils} from "../player.utils";
import {RiskMapService} from "./risk-map.service";
import {TimerService} from "./timer.service";
import * as M from "../../vendor/materialize";

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
        let callback = PlayerUtils.getActivePhaseName();
        TimerService.changeTimer(message, callback);
    }

    logout() {

    }
}