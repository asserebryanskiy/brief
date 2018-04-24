package com.name.brief.model.games;

/**
 * Defines how players of the gameSession are authenticated.
 */
public enum AuthenticationType {
    /**
     * Creates and saves new player on player login attempt.
     */
    CREATE_NEW(
            new String[][]{},
            "Только код сессии"),
    /**
     * Collects player's name and surname before creating and saving player.
     */
    NAME_SURNAME(
            new String[][]{
                    {"name", "Имя", "text"},
                    {"surname", "Фамилия", "text"},
            },
            "+ Имя и фамилия"),
    /**
     * Collects player's name before creating and saving player.
     */
    NAME(
            new String[][]{
                    {"name", "Имя", "text"},
            },
            "+ Имя"),
    /**
     * Collects player's command name.
     * If command is already logged in forbids logging in.
     */
    COMMAND_NAME(
            new String[][]{
                    {"commandName", "Номер команды", "number"},
            },
            "+ Номер команды");

    // where in every array [0] is field name, [1] is russian placeholder and [3] is type of the input
    private final String[][] inputs;
    private final String russianName;

    AuthenticationType(String[][] inputs, String russianName) {
        this.inputs = inputs;
        this.russianName = russianName;
    }

    public String[][] getInputs() {
        return inputs;
    }

    public String getRussianName() {
        return russianName;
    }
}
