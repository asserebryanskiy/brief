export default class BriefUtils {
    static getRoundOrder($round) {
        const roundId = $round.attr('id');
        return parseInt(roundId.substr(roundId.indexOf('-') + 1));
    }

    static setTimersOriginalValues() {
        $('.timer').each(function () {
            $(this).text($(this).parent().children('.timer-original-value').text());
        })
    }
}