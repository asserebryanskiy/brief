import $ from 'jquery'

export default class TimerUtils {
    static convertToTimerString(min, sec) {
        min = min < 10 ? '0' + min : min;
        sec = sec < 10 ? '0' + sec : sec;

        return min + ':' + sec;
    }

    static getMinutes(text) {
        return Math.floor(parseInt(text) / 60);
    }

    static getSeconds(text) {
        return parseInt(text) % 60;
    }

    static playerTimerIsRunning() {
        return $('.phase-container.active').find('.timer').text() !== '00:00';
    }
}