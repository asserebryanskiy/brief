import SockJS from "sockjs-client";
import * as Stomp from "@stomp/stompjs/index";

export default class WsService {
    constructor(gameId, gameIdentifier) {
        this.gamePath = '/app/' + gameIdentifier + '/' + gameId + '/';
        const socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
    }

    send(destination, message) {
        this.stompClient.send(destination, {}, message);
    }

    sendToGame(instruction, message) {
        this.stompClient.send(this.gamePath + instruction, {}, message);
    }

    subscribe(destination, onMessageReceived) {
        this.stompClient.subscribe(destination, onMessageReceived);
    }

    connect(onConnectionCallback) {
        function reconnect() {
            location.reload();
            /*let connected = false;
            let reconInv = setInterval(() => {
                const socket = new SockJS('/websocket');
                stompClient = Stomp.over(socket);
                stompClient.connect({}, () => {
                    clearInterval(reconInv);
                    connected = true;
                    onConnectionCallback();
                }, () => {
                    if (connected) {
                        reconnect();
                    }
                });
            }, 1000);*/
        }

        this.stompClient.connect({}, onConnectionCallback, () => reconnect());
    }
}