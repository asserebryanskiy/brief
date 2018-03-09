package com.name.brief.web.dto;

import lombok.Data;

@Data
public class NextPhaseMessage {
    private int phaseNumber;
    private long timerDuration;  // in seconds
    private String additional;  // is used to add some additional information like correct answer

    public NextPhaseMessage() {
    }

    public NextPhaseMessage(int phaseNumber, int timerDuration) {
        this.phaseNumber = phaseNumber;
        this.timerDuration = timerDuration;
    }
}
