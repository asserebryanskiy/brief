import $ from "jquery";
import AnswerService from "./answer-service";

export default class RolePlayController {
    constructor(wsService) {
        this.wsService = wsService;
    }

    static changePhaseByName(phaseName) {
        const phaseId = this.getPhaseIdByName(phaseName);

        if (phaseId !== -1) {
            console.log('setting ' + phaseId);
            $('.phase-container').hide();
            $('#' + phaseId).show();
        }
    }

    static getPhaseIdByName(phaseName) {
        let phaseId = -1;
        switch (phaseName) {
            case "FORM_GAME":
                phaseId = 'form-game-phase';
                break;
            case "CONNECT_PLAYERS":
                phaseId = 'connect-players-phase';
                break;
            case "SEND_ROLES":
                phaseId = 'send-roles-phase';
                break;
            case "SEND_INSTRUCTION":
                phaseId = 'send-instructions-phase';
                break;
            case "CROSSING":
            case "CROSSING_2":
                phaseId = 'crossing-phase';
                break;
            case "GAME":
            case "GAME_2":
                phaseId = 'game-phase';
                break;
            case "SURVEY":
            case "SURVEY_2":
            case "RESULTS":
            case "DRUGS_DISTRIBUTION":
                phaseId = -1;
                break;
            case "SURVEY_SALESMAN":
                phaseId = 'salesman-survey-phase';
                break;
            case "SURVEY_DOCTOR":
                phaseId = 'doctor-survey-phase';
                break;
            case "RESULTS_AVERAGE":
                phaseId = 'average-results-phase';
                break;
            case "RESULTS_SALESMAN":
                phaseId = 'salesman-results-phase';
                break;
            case "DISCUSSION":
                phaseId = 'discussion-phase';
                break;
            case "CHANGE_ROLES":
                phaseId = 'change-roles-phase';
                break;
            case "DRUGS_DISTRIBUTION_SURVEY":
                phaseId = 'drugs-distribution-phase';
                break;
            case "EXPECTATION":
                phaseId = 'expectation-phase';
                break;
        }
        return phaseId;
    }

    handleEeAnswerVariantClick(event) {
        const $variant = $(event.currentTarget);
        $variant.siblings('.ee-answer-variant').removeClass('selected');
        $variant.addClass('selected');
        this.sendDoctorAnswers();
    }

    sendDoctorAnswers() {
        this.wsService.sendToApp('doctor/answer', AnswerService.createDoctorAnswerJson());
    }

    handleDoctorAnswerSend() {
        this.sendDoctorAnswers();
        RolePlayController.addInstantMessage('Ответы отправлены');
    }

    static handleInstructionMessageReceived(message) {
        const json = JSON.parse(message.body);
        $('.role-name').text(json['roleName']);
        $('.instruction').add($.parseHTML(json['instruction']));
    }

    handleSalesmanAnswerSend() {
        this.wsService.sendToApp('salesman/answer', AnswerService.createSalesmanAnswerJson());
        RolePlayController.addInstantMessage('Ответы отправлены');
    }

    static addInstantMessage(message) {
        $('.instant-message')
            .text(message)
            .addClass('success')
            .slideDown()
            .delay(2000)
            .slideUp();
    }

    static handleOpenDrugDistributionHelpPopup() {
        $('.drugs-distribution-help-popup').show();
    }

    handleDrugsDistributionInputChange(event) {
        const $input = $(event.currentTarget);
        const val = parseInt($input.val());
        let total = 0;
        $('.drugs-distribution-input').each((i, el) => {
            const inputVal = $(el).val();
            total += inputVal.length === 0 ? 0 : parseInt(inputVal);
        });
        if (total > 12) {
            const diff = total - 12;
            $input.val(val - diff);
        }
    }

    sendDrugsDistribution() {
        let dto = {
            drugPackages: []
        };

        $('.drugs-distribution-input').each((i, el) => {
            const inputVal = $(el).val();
            dto.drugPackages[i] = inputVal.length === 0 ? 0 : parseInt(inputVal);
        });

        this.wsService.sendToApp('drugDistribution', JSON.stringify(dto));
        RolePlayController.addInstantMessage('Ответы отправлены')
    }

    static handleSalesmanResultsReceived(message) {
        const dto = JSON.parse(message.body);
        console.log(dto);

        // set competencies average
        for (let competency in dto.competenciesAverage) {
            $('.' + competency + '-result').text(dto.competenciesAverage[competency]);
        }

        // set survey answers
        const numberOfRounds = dto.playerAnswersPerRound["patient-number"].length;
        for (let i = 0; i < numberOfRounds; i++) {
            const $roundResults = $('.round-results-' + i);
            for (let answerType in dto.playerAnswersPerRound) {
                $roundResults.find('.' + answerType + '-player-answer')
                    .text(dto.playerAnswersPerRound[answerType][i]);
                $roundResults.find('.' + answerType + '-correct-answer')
                    .text(dto.correctAnswersPerRound[answerType][i]);
                const $successRate = $roundResults.find('.' + answerType + '-success-rate');
                $successRate.text(dto.successRatePerRound[answerType][i])
                    .parents('.success-rate-wrapper')
                    .removeClass('success-rate-top success-rate-high success-rate-nearly-high success-rate-mid ' +
                        'success-rate-low success-rate-very-low')
                    .addClass('success-rate-' + dto.successRateCssClassPerRound[answerType][i]);
            }
        }

        // set comments
        $('.comment').not('.comment-template').remove();
        dto.comments.forEach((text) => {
            if (text.length > 0) {
                const $template = $('.comment-template');
                const $comment = $template.clone();
                $comment.text(text)
                    .insertAfter($template)
                    .removeClass('hidden comment-template');
            }
        })
    }
}