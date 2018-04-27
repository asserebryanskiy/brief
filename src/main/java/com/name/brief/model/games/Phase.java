package com.name.brief.model.games;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class Phase {
    private final String name;
    private final boolean hasTimer;
    private int id;
    private Duration timerDuration;         // in seconds
    private String englishName;

    public Phase(String name, String englishName, boolean hasTimer) {
        this.name = name;
        this.hasTimer = hasTimer;
        this.englishName = englishName;
    }

    public Phase(String name, String englishName, boolean hasTimer, Duration timerDuration) {
        this(name, englishName, hasTimer);
        this.timerDuration = timerDuration;
    }

    public String getStrTimerDuration() {
        if (!hasTimer) throw new IllegalArgumentException("Phase has no timer");
        return String.format("%02d:%02d", timerDuration.toMinutes(), timerDuration.getSeconds() % 60);
    }

    public String getEnglishName() {
        return this.englishName;
    }
}
