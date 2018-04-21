package com.name.brief.model.games;

/**
 * Defines how players of the gameSession are authenticated.
 */
public enum AuthenticationType {
    /**
     * Creates and saves new player on player login attempt.
     */
    CREATE_NEW,
    /**
     * Collects player's name and surname before creating and saving player.
     */
    NAME_SURNAME,
    /**
     * Collects player's name before creating and saving player.
     */
    NAME,
    /**
     * Collects player's command name.
     * If command is already logged in forbids logging in.
     */
    COMMAND_NAME
}
