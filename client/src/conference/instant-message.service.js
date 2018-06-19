import $ from "jquery";

export class InstantMessageService {
    static addInstantMessage(message, statusClass) {
        $('.instant-message')
            .text(message)
            .removeClass('success failure')
            .addClass(statusClass)
            .slideDown()
            .delay(2000)
            .slideUp();
    }
}