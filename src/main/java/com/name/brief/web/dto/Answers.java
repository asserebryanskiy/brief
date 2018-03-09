package com.name.brief.web.dto;

import lombok.Data;


@Data
public class Answers {
    private String username;
    private String answerStr;
    private int score;

    public Answers() {
    }

    public Answers(String username, String answerStr) {
        this.username = username;
        this.answerStr = answerStr;
    }
}
