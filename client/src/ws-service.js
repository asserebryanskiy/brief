import SockJS from "sockjs-client";
import { Stomp } from "../vendor/stomp";

export default class WsService {
    constructor(gameId, gameIdentifier) {
        this.gamePath = '/app/' + gameIdentifier + '/' + gameId + '/';
        this.gameTopic = '/topic/' + gameIdentifier + '/' + gameId + '/';
        const socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
    }

    send(destination, message) {
        this.stompClient.send(destination, {}, message);
    }

    sendToGameTopic(instruction, message) {
        this.stompClient.send(this.gameTopic + instruction, {}, message);
    }

    sendToApp(instruction, message) {
        this.stompClient.send(this.gamePath + instruction, {}, message);
    }

    subscribe(destination, onMessageReceived) {
        this.stompClient.subscribe(destination, onMessageReceived);
    }

    connect(onConnectionCallback) {
        console.log('I`ve updated 2!');
        let stompClient = this.stompClient;
        function reconnect() {
            console.log('reconnecting...');
            // location.reload();
            let connected = false;
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
            }, 1000);
        }

        this.stompClient.connect({}, onConnectionCallback, () => {
            console.log('lost connection');
            location.reload(true);
        });
    }
}