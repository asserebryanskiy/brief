import GameSessionUtils from "../../game-session-utils";
import $ from 'jquery';
import {PlayerUtils} from "../../player.utils";
import {InstantMessageService} from "../instant-message.service";

export class SelfAnalysisComponent {

    constructor() {
        this.getAnswers();
    }

    static handleSendAnswersClicked() {
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
                // show instant message
                InstantMessageService.addInstantMessage('Ваши ответы отправлены', 'success');

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
    }

    static collectData() {
        const answers = [];
        $('.self-analysis-answer-input').each((i, el) => {
            answers.push($(el).val());
        });
        const id = $('#self-analysis-id-input').text();
        const data = JSON.stringify({
            id: id.length ? id : null,
            participantId: PlayerUtils.getPlayerId(),
            answers: answers,
            readyToShare: $('.self-analysis-ready-to-share-checkbox').prop('checked')
        });
        return data;
    }

    getAnswers() {
        const url = '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis/participant/' + PlayerUtils.getPlayerId();
        $.getJSON(url, data => {
            console.log(data);
            if (data) {
                $('.self-analysis-answer-input').each((i, el) => {
                    $(el).val(data.answers[i]);
                });
                $('.self-analysis-ready-to-share-checkbox').prop('checked', data.readyToShare);
                $('#self-analysis-id-input').text(data.id);

                const $btn = $('.self-analysis-send-btn');
                $btn.hide();
                $btn.siblings('.btn-pane').removeClass('hide').show();
            }
        });
    }

    static handleChangeAnswersClicked() {
        $.ajax({
            url: '/api/conference/' + GameSessionUtils.getGameId() + '/selfAnalysis',
            method: 'PUT',
            data: this.collectData(),
            dataType: 'json',
            contentType: 'application/json',
            success: (data) => {
                InstantMessageService.addInstantMessage('Ответы обновлены', 'success');
            },
            error: err => {
                console.log(err);
                InstantMessageService.addInstantMessage('Произошла ошибка. Не удалось обновить ваши ответы', 'failure');
            }
        })
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
}