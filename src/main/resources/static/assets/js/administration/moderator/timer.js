function Timer(stompClient) {
    // FIELDS
    this.stompClient = stompClient;
    this.timerId = 0;
    this.timerSubscription = null;

    // METHODS
    this.startTimer = ($timer, onFinishedCallback) => {
        this.timerId = window.setInterval(() => {
            this.decreaseTimer($timer, onFinishedCallback);
        }, 1000);
        this.timerSubscription = this.stompClient.subscribe('/topic/' + gameSessionId + '/timer', (message) => {
            $timer.text(message.body);
        }, {});
        this.stompClient.send('/app/' + gameSessionId + '/startTimer', {}, $timer.text())
    };

    this.clearTimers = () => {
        window.clearInterval(this.timerId);
        const $timer = $('.timer');
        $timer.removeAttr('style');
        $timer.show();
        $('.add-30-sec-btn').hide();
        if (this.timerSubscription != null) this.timerSubscription.unsubscribe();
    };

    this.decreaseTimer = ($timer, onFinishedCallback) => {
        const timeStr = $timer.text();
        const dividerInd = timeStr.indexOf(':');
        let min = parseInt(timeStr.slice(0, dividerInd));
        let sec = parseInt(timeStr.substr(dividerInd + 1));
        if (min === 0 && sec === 0) {
            // stop timer
            window.clearInterval(this.timerId);
            this.timerSubscription.unsubscribe();

            onFinishedCallback();
            return;
        }
        if (min === 0 && sec < 12) {
            $timer.css('color', 'tomato');
        }
        if (min > 0 && sec === 0) {
            min--;
            sec = 60;
        }
        sec = --sec < 10 ? '0' + sec : sec;
        min = min < 10 ? '0' + min : min;
        this.stompClient.send('/topic/' + gameSessionId + '/timer', {}, `${min}:${sec}`);
    }
}
