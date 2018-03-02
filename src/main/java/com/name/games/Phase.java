package com.name.games;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phase {
    private final int order;
    private final String name;
    private final boolean hasTimer;
    private String timerCount;         // in seconds

    public Phase(int order, String name, boolean hasTimer) {
        this.order = order;
        this.name = name;
        this.hasTimer = hasTimer;
    }

    public Phase(int order, String name, boolean hasTimer, String timerCount) {
        this(order, name, hasTimer);
        this.timerCount = timerCount;
    }

}
