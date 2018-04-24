package com.name.brief.model.games;

/**
 * Defines how players of the gameSession are authenticated.
 */
public enum AuthenticationType {
    /**
     * Creates and saves new player on player login attempt.
     */
    CREATE_NEW(
            new String[][]{}
    ),
    /**
     * Collects player's name and surname before creating and saving player.
     */
    NAME_SURNAME(
            new String[][]{
                    {"name", "Имя"},
                    {"surname", "Фамилия"},
            }
    ),
    /**
     * Collects player's name before creating and saving player.
     */
    NAME(
            new String[][]{
                    {"name", "Имя"},
            }
    ),
    /**
     * Collects player's command name.
     * If command is already logged in forbids logging in.
     */
    COMMAND_NAME(
            new String[][]{
                    {"commandName", "Номер команды"},
            }
    );

    private final String[][] inputs;

    AuthenticationType(String[][] inputs) {
        this.inputs = inputs;
    }

    public String[][] getInputs() {
        return inputs;
    }
}
