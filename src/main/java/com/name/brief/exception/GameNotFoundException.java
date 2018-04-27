package com.name.brief.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game with id " + gameId + " was not found");
    }
}
