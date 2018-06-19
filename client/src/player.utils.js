import $ from 'jquery';

export class PlayerUtils {
    static getCssClassFromConstant(constant) {
        return constant.toLowerCase().split('_').join('-');
    }

    static getActivePhaseName() {
        const id = $('.phase-container.active').attr('id');
        return id.slice(0, id.indexOf('-phase'));
    }

    static getPlayerId() {
        return parseInt($('#player-id').text());
    }
}