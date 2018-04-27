import $ from "jquery";

export default class RolePlayController {
    constructor(wsService) {
        this.wsService = wsService;
    }

    changePhaseByName(phaseName) {
        let phaseIndex;
        switch (phaseName) {
            case "FORM_GAME":
                phaseIndex = 0;
                break;
            case "CONNECT_PLAYERS":
                phaseIndex = 1;
                break;
            case "SEND_ROLES":
                phaseIndex = 2;
                break;
            case "SEND_INSTRUCTION":
                phaseIndex = 3;
                break;
            case "CROSSING":
                phaseIndex = 4;
                break;
            case "GAME":
                phaseIndex = 5;
                break;
            case "SURVEY":
                phaseIndex = 6;
                break;
            case "SURVEY_EXPECTATION":
                phaseIndex = 7;
                break;
        }

        this.changePhaseByIndex(phaseIndex);
    }

    changePhaseByIndex(phaseIndex) {
        $('.phase-container').hide();
        $('#phase-' + phaseIndex).show();
    }
}