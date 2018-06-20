import $ from 'jquery';
import Swiper from "swiper";
import GameSessionUtils from "../../game-session-utils";
import {PlayerUtils} from "../../player.utils";
import {InstantMessageService} from "../instant-message.service";
import TimerUtils from "../../roleplay/TimerUtils";

export class GreetingComponent {
    constructor(swiper) {
        this.imgIndex = 0;
        this.swiper = swiper;
        this.getData();
        $('.greeting-choose-btn').click(event => this.handleImageChosen(event));
        $('.greeting-change-chose-btn').click(event => this.handleChangeChoice(event));
        $('.greeting-send-btn').click(event => this.sendResponses());
    }

    handleImageChosen(event) {
        this.imgIndex = $(event.currentTarget).siblings('img').attr('id').slice('greeting-img-'.length);
        this.showCommentInput();
    }

    showCommentInput() {
        $('#greeting-phase .hide-after-chose').hide();
        $('#greeting-phase .show-after-chose').removeClass('hide').show();
        $('#greeting-why-img-input').focus();
        this.swiper.allowTouchMove = false;
    }

    handleChangeChoice(event) {
        $('#greeting-phase .hide-after-chose').show();
        $('#greeting-phase .show-after-chose').hide();
        this.swiper.allowTouchMove = true;
    }

    createNewAnswer() {
        $.ajax({
            url: '/api/conference/greeting',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                conferenceId: GameSessionUtils.getGameId(),
                participantId: PlayerUtils.getPlayerId(),
                imgIndex: this.imgIndex,
                comment: $('#greeting-why-img-input').val()
            }),
            success: (id) => {
                $('#greeting-answer-id-input').text(id);
                InstantMessageService.addInstantMessage('Ваш ответ успешно отправлен.', 'success')
            }
        })
    }

    updateAnswer() {
        $.ajax({
            url: '/api/conference/greeting/' + $('#greeting-answer-id-input').text(),
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                conferenceId: GameSessionUtils.getGameId(),
                participantId: PlayerUtils.getPlayerId(),
                imgIndex: this.imgIndex,
                comment: $('#greeting-why-img-input').val()
            }),
            error: err => {
                console.log(err);
                InstantMessageService.addInstantMessage('Произошла ошибка. Не удалось обновить ответы', 'failure');
            },
            success: () => InstantMessageService.addInstantMessage('Ваш ответ успешно обновлен.', 'success')
        })
    }

    sendResponses() {
        if (TimerUtils.playerTimerIsRunning()) {
            if ($('#greeting-answer-id-input').text())
                this.updateAnswer();
            else
                this.createNewAnswer();
        } else {
            InstantMessageService.addInstantMessage('Время вышло. Невозможно отправить ответы.', 'failure')
        }
    }

    getData() {
        $.getJSON('/api/conference/greeting', {
            conferenceId: GameSessionUtils.getGameId(),
            participantId: PlayerUtils.getPlayerId()
        }, data => {
            console.log(data);
            if (data != null) {
                this.imgIndex = data.imgIndex;
                $('#greeting-why-img-input').val(data.comment);
                $('#greeting-answer-id-input').text(data.greetingId);
                this.showCommentInput();
                this.swiper.slideTo(data.imgIndex + 1);
            }
        })
    }
}