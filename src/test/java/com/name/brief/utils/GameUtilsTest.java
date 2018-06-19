package com.name.brief.utils;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.Phase;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GameUtilsTest {
    @Test
    public void getPhaseIndexByName_returnsIndexOfPhase() {
        List<Phase> phases = Conference.phases;
        for (int i = 0; i < phases.size(); i++) {
            Phase phase = phases.get(i);
            assertThat(GameUtils.getPhaseIndexByName(new Conference(), phase.getEnglishName()),
                    is(i));
        }
    }
}