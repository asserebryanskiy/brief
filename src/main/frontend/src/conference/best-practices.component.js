import $ from 'jquery';
import {PlayerUtils} from "../player.utils";
import GameSessionUtils from "../game-session-utils";
import {InstantMessageService} from "./instant-message.service";
import * as M from "../../vendor/materialize";
import TimerUtils from "../roleplay/TimerUtils";

export class BestPracticesComponent {

    constructor(wsService) {
        this.wsService = wsService;
        this.playerId = PlayerUtils.getPlayerId();

        $.getJSON('/api/conference/bestPractice/participant/' + this.playerId, (data) => {
            for (let practice of data) this.addBestPractice(practice);
        })
    }

    handleSendBtnClicked(event) {
        if (TimerUtils.playerTimerIsRunning()) {
            const $input = $('#best-practices-input');
            const text = $input.val();
            if (!text.length) return;

            const bestPracticeDto = JSON.stringify({
                text: text,
                participantId: PlayerUtils.getPlayerId(),
            });

            // this.sendBestPractice(text);
            this.post('', bestPracticeDto, (data) => {
                this.addBestPractice(data);
                $('.best-practices-send-btn').removeClass('ready-btn')
                    .addClass('change-btn')
                    .text('Отправить еще одну');
                $input.val('');
                M.textareaAutoResize($input);
                InstantMessageService.addInstantMessage('Отправлено!', 'success');
            })
        } else {
            InstantMessageService.addInstantMessage('Время вышло. Невозможно отправить ответы.', 'failure')
        }
    }

    addBestPractice(dto) {
        const $wrapper = $('.sent-best-practices');
        $wrapper.find('#best-practices-list-placeholder').hide();
        $('#edit-best-practice-hint').removeClass('hide').show();
        const $newPractice = $('.best-practice-template').clone();
        $newPractice.removeClass('best-practice-template hide');
        $newPractice.find('.best-practice-text')
            .text(dto.text)
            .attr('id', 'best-practice-text-' + dto.id);

        $wrapper.find('.best-practices-list').append($newPractice);

        const $trigger = $newPractice.find('.dropdown-trigger');
        const dropdownId = 'best-practice-dropdown-' + dto.id;
        $trigger.attr('data-target', dropdownId);
        const $dropdown = $trigger.siblings('.dropdown-content');
        $dropdown.attr('id', dropdownId);
        M.Dropdown.init($trigger, {
            alignment: "right",
            constrainWidth: false,
            coverTrigger: false,
            container: $wrapper
        });

        $dropdown.find('.edit-best-practice-btn').click(event =>
            BestPracticesComponent.prepareBestPracticeEditing(dto.text, dto.id));
        $dropdown.find('.delete-best-practice-btn').click(event => {
            this.deleteBp(dto.id);
        })
    }

    getOnTimerEndedCallback() {
        return () => {
            console.log('timer ended');
        }
    }

    static prepareBestPracticeEditing(text, id) {
        const $input = $('#best-practices-input');
        $input.val(text);
        $('#best-practice-id-input').text(id);
        M.textareaAutoResize($input);
        $input.siblings('label').addClass('active');
        $input.click();

        const $btnWrapper = $('.edit-best-practice-buttons-wrapper');
        $btnWrapper.removeClass('hide').show();
        $btnWrapper.siblings('.best-practices-send-btn').hide();
    }

    handleChangeBtnClicked(event) {
        if (TimerUtils.playerTimerIsRunning()) {
            const $input = $('#best-practices-input');
            const text = $input.val();

            this.post('/' + $('#best-practice-id-input').text(), JSON.stringify({text: text}), (data) => {
                $('#best-practice-text-' + data.id).text(data.text);
                const $btnWrapper = $('.edit-best-practice-buttons-wrapper');
                $btnWrapper.hide();
                $btnWrapper.siblings('.best-practices-send-btn').show();
                $input.siblings('label').removeClass('active');
                $input.val('');
                M.textareaAutoResize($input);
                InstantMessageService.addInstantMessage('Лучшая практика успешно изменена', 'success');
            })
        } else {
            InstantMessageService.addInstantMessage('Время вышло. Невозможно отправить ответы.', 'failure')
        }
    }

    post(uri, bestPracticeDto, successHandler) {
        const wrapper = $('.sent-best-practices');
        wrapper.find('.hide-on-sending').hide();
        wrapper.find('.sending-preloader-wrapper').removeClass('hide').show();
        $.post({
            url: '/api/conference/' + GameSessionUtils.getGameId() + '/bestPractice' + uri,
            data: bestPracticeDto,
            dataType: 'json',
            contentType: 'application/json',
            success: data => {
                console.log(data);
                window.setTimeout(() => {
                    wrapper.find('.hide-on-sending').show();
                    wrapper.find('.sending-preloader-wrapper').hide();
                    successHandler(data);
                }, 1000);
            },
            error: err => {
                console.log(err);
                wrapper.find('.hide-on-sending').show();
                wrapper.find('.sending-preloader-wrapper').hide();
                $('#best-practices-input').val(bestPracticeDto.text);
                InstantMessageService.addInstantMessage(
                    'Произошла ошибка. Не удалось отправить лучшую практику.', 'failure');
            }});
    }

    static cancelBpChange(event) {
        const $input = $('#best-practices-input');
        $input.val('');
        $('#best-practice-id-input').text('');
        M.textareaAutoResize($input);
        $input.siblings('label').removeClass('active');

        const $btnWrapper = $('.edit-best-practice-buttons-wrapper');
        $btnWrapper.hide();
        $btnWrapper.siblings('.best-practices-send-btn').show();
    }

    deleteBp(id) {
        const wrapper = $('.sent-best-practices');
        wrapper.find('.hide-on-sending').hide();
        wrapper.find('.sending-preloader-wrapper').removeClass('hide').show();
        $.ajax({
            url: '/api/conference/' + GameSessionUtils.getGameId() + '/bestPractice/' + id,
            method: 'DELETE',
            success: () => {
                window.setTimeout(() => {
                    wrapper.find('.hide-on-sending').show();
                    wrapper.find('.sending-preloader-wrapper').hide();
                    InstantMessageService.addInstantMessage(
                        'Лучшая практика удалена', 'success');
                    $('#best-practice-text-' + id).parents('.best-practice-wrapper').remove();
                    if ($('.best-practice-wrapper').length === 1) {
                        $('#best-practices-list-placeholder').show();
                        $('#edit-best-practice-hint').hide();
                        $('.best-practices-send-btn').removeClass('change-btn')
                            .addClass('ready-btn')
                            .text('Отправить');
                    }
                }, 1000);
            }
        })
    }
}