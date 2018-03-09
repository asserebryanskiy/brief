package com.name.brief.games;

import com.name.brief.model.games.Phase;
import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PhaseTest {
    @Test
    public void getStrTimerDuration_returnsProperlyFormattedStr() {
        Phase phase = new Phase("test", true, Duration.ofSeconds(90));

        assertThat(phase.getStrTimerDuration(), is("01:30"));
    }

    @Test
    public void getStrTimerDuration_returnsProperlyFormattedStrIfZeroMinutes() {
        Phase phase = new Phase("test", true, Duration.ofSeconds(30));

        assertThat(phase.getStrTimerDuration(), is("00:30"));
    }
}