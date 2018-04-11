package com.name.brief.exception;

public class WrongGameTypeException extends Exception {
    public WrongGameTypeException(String actual, String expected) {
        super(String.format("Expected %s but was %s%n", expected, actual));
    }
}
