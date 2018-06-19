import {RiskMapService} from "../risk-map.service";
import $ from "jquery";
import {PlayerUtils} from "../../player.utils";

export class ProjectorConferenceController {
    changePhaseByName(phaseName) {
        $('.phase-container').removeClass('active');
        const phaseCssId = '#' + PlayerUtils.getCssClassFromConstant(phaseName) + '-phase';
        console.log(phaseCssId);
        $(phaseCssId).addClass('active');
    }
}