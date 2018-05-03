package com.name.brief.exception;

public class OddNumberOfPlayersException extends Exception {
    public OddNumberOfPlayersException() {
        super("Could not proceed game with odd number of players");
    }
}
