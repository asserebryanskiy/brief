package com.name.games.brief;

public enum Round {
    FIRST("A1"),
    SECOND("B2"),
    THIRD("C3"),
    FOURTH("D4"),
    FIFTH("A2");

    private String correctAnswer;

    Round(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
