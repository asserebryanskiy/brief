package com.name.brief.exception;

public class PlayerAuthenticationFailedException extends RuntimeException {
    public PlayerAuthenticationFailedException(String message) {
        super(message);
    }
}
