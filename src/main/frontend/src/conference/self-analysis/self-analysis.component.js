import GameSessionUtils from "../../game-session-utils";
import $ from 'jquery';
import {PlayerUtils} from "../../player.utils";
import {InstantMessageService} from "../instant-message.service";
import TimerUtils from "../../roleplay/TimerUtils";

export class SelfAnalysisComponent {

    constructor() {
        this.getAnswers();
    }

    static handleSendAnswersClicked() {
        if (TimerUtils.playerTimerIsRunning()) {
            // hide send btn
            const $btn = $('.self-analysis-send-btn');
            $btn.hide();

            // show preloader
            $('.self-analysis-preloader-wrapper').removeClass('hide').show();

            // gather answers
            const data = this.collectData();
            console.log(data);
            $.ajax({
                url: '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis',
                method: 'POST',
                data: data,
                dataType: 'json',
                contentType: 'application/json',
                success: (data) => {
                    // block inputs
                    $('.self-analysis-answer-input').prop('disabled', true);

                    // show instant message
                    InstantMessageService.addInstantMessage('Ваши ответы сохранены', 'success');

                    // hide preloader
                    $('.self-analysis-preloader-wrapper').hide();

                    // show pdf and change btn tab
                    $btn.siblings('.btn-pane').removeClass('hide').show();
                },
                error: err => {
                    console.log(err);
                    $btn.show();
                    $('.self-analysis-preloader-wrapper').hide();

                    InstantMessageService.addInstantMessage('Произошла ошибка. Не удалось отправить ваши ответы', 'failure');
                }
            })
        } else {
            InstantMessageService.addInstantMessage('Время вышло. Невозможно отправить ответы.', 'failure')
        }
    }

    static collectData() {
        const answers = [];
        $('.self-analysis-answer-input').each((i, el) => {
            answers.push($(el).val());
        });
        const id = $('#self-analysis-id-input').text();
        return JSON.stringify({
            id: id.length ? id : null,
            participantId: PlayerUtils.getPlayerId(),
            answers: answers,
            readyToShare: $('.self-analysis-ready-to-share-checkbox').prop('checked')
        });
    }

    getAnswers() {
        const url = '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis/participant/' + PlayerUtils.getPlayerId();
        $.getJSON(url, data => {
            console.log(data);
            if (data) {
                const $input = $('.self-analysis-answer-input');
                $input.each((i, el) => {
                    $(el).val(data.answers[i]);
                });
                $('.self-analysis-ready-to-share-checkbox').prop('checked', data.readyToShare);
                $('#self-analysis-id-input').text(data.id);

                const $btn = $('.self-analysis-send-btn');
                $btn.hide();
                $btn.siblings('.btn-pane').removeClass('hide').show();
                $input.prop('disabled', true);
            }
        });
    }

    static handleChangeAnswersClicked(event) {
        if (TimerUtils.playerTimerIsRunning()) {
            const $btn = $(event.currentTarget);
            if ($btn.hasClass('change-btn')) {
                $btn.removeClass('change-btn')
                    .addClass('ready-btn')
                    .text('Отправить');
                $('.self-analysis-answer-input').prop('disabled', false);
            } else {
                $.ajax({
                    url: '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis',
                    method: 'PUT',
                    data: this.collectData(),
                    dataType: 'json',
                    contentType: 'application/json',
                    success: (data) => {
                        InstantMessageService.addInstantMessage('Ответы обновлены', 'success');
                        this.afterDataSent();
                    },
                    error: err => {
                        console.log(err);
                        InstantMessageService.addInstantMessage('Произошла ошибка. Не удалось обновить ваши ответы', 'failure');
                    }
                })
            }
        } else {
            InstantMessageService.addInstantMessage('Время вышло. Невозможно отправить ответы.', 'failure')
        }
    }

    static getPdf() {
        // const xhr = new XMLHttpRequest();
        const url = '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis/participant/' + PlayerUtils.getPlayerId() + '/pdf';
        // xhr.open(url, )
        console.log('sending pdf request..');
        $.ajax({
            url: url,
            method: 'GET',
            contentType: 'application/pdf',
            success: (data) => {
                console.log('success! received pdf!');
                const blob = new Blob([data]);
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = "Sample.pdf";
                link.click();
            },
            error: (err) => {
                console.log('failure!');
                console.log(err)
            }
        })
    }

    static afterDataSent() {
        $('.change-self-analysis-btn').removeClass('ready-btn')
            .addClass('change-btn')
            .text('Изменить');
        $('.self-analysis-answer-input').prop('disabled', true);
    }
}