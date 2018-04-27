import Timer from "./timer-1.0.1"

export default class RolePlayController {
    constructor(stompClient) {
        this.stompClient = stompClient;
        this.timer = new Timer(this.stompClient);
    }
}