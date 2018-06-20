import RolePlayController from "../roleplay/game.role-play-controller";
import $ from "jquery";
import TimerUtils from "../roleplay/TimerUtils";

export class TimerService {
    static changeTimer(message, callback) {
        const $timer = $('.timer');

        // parse incoming data
        let sec = TimerUtils.getSeconds(message.body);
        let min = TimerUtils.getMinutes(message.body);

        // if timer is finishing make it red
        if (min === 0 && sec < 11) $timer.addClass('last-ten-seconds');
        else $timer.removeClass('last-ten-seconds');

        // if time has ended notify user
        if (min === 0 && sec === 0) {
            callback();
        }

        // change timer text
        $timer.text(TimerUtils.convertToTimerString(min, sec));
    }
}