import $ from "jquery";
import {RiskMapUtils} from "./risk-map.utils";

export class RiskMapService {
    constructor() {
        this.answerInputBlocked = false;
    }

    static handleSmallImgClicked(event) {
        const $target = $(event.currentTarget);
        const imgId = $target.find('img').attr('id').substr(0, 2);
        const $popup = $target.parent().find('.popup-wrapper');
        const $popupContent = $popup.find('.popup-content');
        if ($popupContent.children('img').length === 0) {
            console.log('entered');
            const $placeholder = $popupContent.children('.img-placeholder');
            $('#' + imgId + '-large-img').insertBefore($placeholder);
            $placeholder.remove();
            // $popupContent.children('.answer-inputs').insertAf.append($('#' + imgId + '-large-img'))
        }
        $popup.show();
    }

    handleAnswerInputClicked(event) {
        if (!this.answerInputBlocked) {
            const $target = $(event.currentTarget);
            // clear self-made radio-buttons
            $target.siblings().removeClass('selected');

            // toggle selection of clicked input
            $target.toggleClass('selected');

            // remove indicator from img-cell if any
            const $indicator = $('.' + $target.parents('.risk-img-cell')[0].classList[1])
                .find('.risk-indicator');
            $indicator.removeClass('no-level low-level mid-level high-level');

            if ($target.hasClass('selected')) {
                // add new indicator to img-cell
                const className = $target.hasClass('low-level') ? 'low-level'
                    : $target.hasClass('no-answer') ? 'no-answer'
                        : $target.hasClass('no-level') ? 'no-level'
                            : $target.hasClass('mid-level') ? 'mid-level' : 'high-level';
                $indicator.addClass(className);
            }
        }
    }

    handleSendResponsesClicked(event) {
        if (!this.answerInputBlocked) {
            $(event.currentTarget)
                .removeClass('ready-btn')
                .addClass('change-btn')
                .text('Изменить ответы');
            RiskMapService.addInstantMessage('Ответы отправлены', 'success');
            this.sendResponses();
        } else {
            RiskMapService.addInstantMessage('Время вышло. Отправка ответов заблокирована', 'failure');
        }
    }

    static addInstantMessage(message, statusClass) {
        $('.instant-message')
            .text(message)
            .removeClass('success failure')
            .addClass(statusClass)
            .slideDown()
            .delay(2000)
            .slideUp();
    }

    static handleResultsPhase() {
        const answerStr = RiskMapUtils.getAnswerStr();
        const answerMatrix = RiskMapUtils.getAnswerMatrix(answerStr);
        // set score text
        const totalScore = RiskMapUtils.getTotalScore(answerMatrix);
        $('#score-text').text(totalScore);
        // set up score on every correct-answer-cover
        $('.correct-answer-cover').each((i, el) => {
            const className = $(el).parents('.risk-img-cell')[0].classList[1];
            const sector = parseInt(className.substr(className.lastIndexOf('-') + 1));
            const row = Math.floor(sector / 4);
            const col = sector % 4;
            const score = RiskMapUtils.getScoreForSector(row, col, answerMatrix[row][col]);
            $(el).find('.sector-score-text').text(score > 0 ? '+' + score : score);
        });

        if (answerStr.length > 0) {
            let acc = '';
            let sector = 0;
            for (let i = 0; i < answerStr.length; i++) {
                const letter = answerStr.charAt(i);
                if (letter === '-') {
                    sector = parseInt(acc);
                    acc = '';
                } else if (letter === ',') {
                    const $circles = $('.risk-img-cell-' + sector).find('.possible-results').find('.correct-answer-circle');
                    $circles.removeClass('selected');
                    $($circles[parseInt(acc) + 1]).addClass('selected');
                    acc = '';
                    sector = 0;
                } else {
                    acc += letter;
                }
            }
            const $circles = $('.risk-img-cell-' + sector).find('.possible-results').find('.correct-answer-circle');
            $circles.removeClass('selected');
            $($circles[parseInt(acc) + 1]).addClass('selected');
        }

        let congratulationText = '';
        if (totalScore < 0) congratulationText = 'Сотрудники все еще в большой опасности! Может, попробуете еще раз?';
        else if (totalScore >= 0 && totalScore < 1000) congratulationText = 'Вы вышли в "плюс", однако не достигли верхних позиций рейтинга. Предлагаем попробовать еще раз!';
        else if (totalScore >= 1000 && totalScore < 1500) congratulationText = 'Хорошая работа! Ваш результат находится в числе 35% лучших в рейтинге.';
        else if (totalScore >= 1500 && totalScore < 1900) congratulationText = 'Поздравляем! Вы вошли в 20% лучших в рейтинге. Вы значительно повысили безопасность в офисе, однако есть еще над чем работать.';
        else congratulationText = 'Великолепно! Вы вошли в 5% лучших в рейтинге. Благодаря вам, офис безопасен и сотрудники могут комфортно работать.';
        $('.congratulation-text').text(congratulationText);
    }

    getOnTimerEndedCallback() {
        this.sendResponses();
        this.answerInputBlocked = true;
    }

    sendResponses() {
        console.log('sending responses..')
    }

    enableAnswerSend() {
        this.answerInputBlocked = false;
    }

    getOnTimerEndedCallback() {
        return () => {
            console.log('timer ended');
        }
    }
}