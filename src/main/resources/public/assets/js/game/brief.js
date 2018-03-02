const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);
let username = '';
stompClient.connect({}, function (frame) {
    username = frame.headers['user-name'];
    console.log(username);
    stompClient.subscribe('/queue/' + username + '/logout', function () {
        stompClient.disconnect();
        window.location = 'http://localhost:8080/';
    }, {});
    stompClient.send("/app/connect", {}, "");
    // Если соединение прервано, нужно сообщить об этом модератору!

});

const showTimer = () => {
    $('.timer').show();
    $('.message-wrapper').hide();
};
