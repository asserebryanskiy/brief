package com.name.brief.model.games;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class Phase {
    private final String name;
    private final boolean hasTimer;
    private Duration timerDuration;         // in seconds

    public Phase(String name, boolean hasTimer) {
        this.name = name;
        this.hasTimer = hasTimer;
    }

    public Phase(String name, boolean hasTimer, Duration timerDuration) {
        this(name, hasTimer);
        this.timerDuration = timerDuration;
    }

    public String getStrTimerDuration() {
        if (!hasTimer) throw new IllegalArgumentException("Phase has no timer");
        return String.format("%02d:%02d", timerDuration.toMinutes(), timerDuration.getSeconds() % 60);
    }
}
