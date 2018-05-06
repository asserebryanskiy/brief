package com.name.brief.model.games;

import com.name.brief.model.Decision;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.DoctorRole;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BriefTest {

    private Decision decision;
    private Game game;

    @Before
    public void setUp() throws Exception {
        decision = new Decision();
        decision.setRoundNumber(0);
        game = new Brief();
    }

    @Test
    public void getScore_returnsZeroOnEmptyInput() {
        decision.setAnswer("");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsZeroOn9Variants() {
        decision.setAnswer("A1C2B4D1A2A4B1B2B3");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsZeroOn1WrongVariant() {
        decision.setAnswer("D1");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsFifteenOn1CorrectVariant() {
        decision.setAnswer("A3");

        assertThat(game.getScore(decision), is(15));
    }

    @Test
    public void getScore_returnsTenOn2VariantsWithCorrect() {
        decision.setAnswer("A3B2");

        assertThat(game.getScore(decision), is(10));
    }

    @Test
    public void getScore_returns5On3VariantsWithCorrect() {
        decision.setAnswer("A3B2C3");

        assertThat(game.getScore(decision), is(5));
    }
}