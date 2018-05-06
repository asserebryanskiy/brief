import $ from "jquery";
import AnswerService from "./answer-service";
import TimerUtils from "./TimerUtils";

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
            $('.' + competency + '-player-result').text(dto.competenciesAverage[competency]);
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
                $roundResults.find('.' + answerType + '-error')
                    .text(dto.errorPerRound[answerType][i])
                    .removeClass('error-very-high error-high error-nearly-high error-mid ' +
                        'error-low error-very-low')
                    .addClass('error-' + dto.errorCssClassPerRound[answerType][i]);
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

    static handleAverageAnswersReceived(message) {
        const dto = JSON.parse(message.body);

        // set competencies average
        for (let competency in dto.averageCompetenciesResults) {
            $('.' + competency + '-average-result').text(dto.averageCompetenciesResults[competency]);
        }

        // set success rates average
        for (let answerType in dto.averageError) {
            $('.' + answerType + '-average-error').text(dto.averageError[answerType])
                .removeClass('error-very-high error-high error-nearly-high error-mid ' +
                    'error-low error-very-low')
                .addClass('error-' + dto.averageErrorCssClass[answerType]);
        }
    }

    static handleScroll() {
        if ($(window).scrollTop() > 35) {
            $('.timer').addClass('fixed-to-top');
        } else {
            $('.timer').removeClass('fixed-to-top');
        }
    }

    static handleTimerMessageReceived(message) {
        const $timer = $('.timer');

        // parse incoming data
        let sec = TimerUtils.getSeconds(message.body);
        let min = TimerUtils.getMinutes(message.body);

        // if timer is finishing make it red
        if (min === 0 && sec < 11) $timer.addClass('last-ten-seconds');
        else $timer.removeClass('last-ten-seconds');

        // change timer text
        $timer.text(TimerUtils.convertToTimerString(min, sec));
    }
}