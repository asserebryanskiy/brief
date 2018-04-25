package com.name.brief.web.dto;

import lombok.Data;

/**
 * Is sent by PlayerAuthenticationService by websocket to the client side.
 *
 * Includes information about an event occurred with a particular player (defined by username):
 *  - player logged in
 *  - player logged out
 *  - player connected to websocket
 *  - player disconnected from websocket
 */
@Data
public class PlayerConnectionDto {
    private PlayerConnectionInstruction instruction;
    private String username;
    private String identifierForModerator;  // what moderator will see as player identifier when player displays

    public PlayerConnectionDto() {
    }

    public PlayerConnectionDto(PlayerConnectionInstruction instruction, String username) {
        this.instruction = instruction;
        this.username = username;
    }

    public enum PlayerConnectionInstruction {
        LOGIN, CONNECT, DISCONNECT, LOGOUT
    }
}
