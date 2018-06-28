import $ from 'jquery'
import GameSessionUtils from "../../game-session-utils";
import {PlayerUtils} from "../../player.utils";
import noUiSlider from 'nouislider';

export class FunComponent {
    constructor() {
        $('.fun-phase-send-btn').click(() => this.send());

        noUiSlider.create(document.getElementById('fun-phase-slider'), {
            start: '5',
            connect: [false, false],
            range: {
                min: 0,
                max: 10
            },
            step: 1,
            tooltips: true
        })
    }

    send() {
        const $preloader = $('#fun-phase .preloader-wrapper');
        $preloader.removeClass('hide').show();
        const $content = $('.fun-content');
        $content.hide();
        $.ajax({
            url: '/api/conference/' + GameSessionUtils.getGameId() + '/fun/' + PlayerUtils.getPlayerId(),
            contentType: 'application/json',
            success: (data) => {
                console.log(data);
                window.setTimeout(() => {
                    $('.fun-phrase').text(data);

                    $content.show();
                    $content.find('.fun-phrase-p').removeClass('hide');

                    $('.fun-phase-send-btn')
                        .removeClass('ready-btn')
                        .addClass('change-btn')
                        .text('Изменить');

                    $preloader.hide();
                }, 1500);
            }
        })
    }
}