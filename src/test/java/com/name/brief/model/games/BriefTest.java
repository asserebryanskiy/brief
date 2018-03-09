package com.name.brief.model.games;

import com.name.brief.model.Decision;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
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
        decision.setRoundNumber(1);
        game = new Brief();
    }

    @Test
    public void getScore_returnsZeroOnEmptyInput() {
        decision.setAnswer("");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsZeroOn4Variants() {
        decision.setAnswer("A1C2B4D1");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsZeroOn1WrongVariant() {
        decision.setAnswer("D1");

        assertThat(game.getScore(decision), is(0));
    }

    @Test
    public void getScore_returnsThreeOn1CorrectVariant() {
        decision.setAnswer("A1");

        assertThat(game.getScore(decision), is(3));
    }

    @Test
    public void getScore_returnsTwoOn2VariantsWithCorrect() {
        decision.setAnswer("A1B2");

        assertThat(game.getScore(decision), is(2));
    }

    @Test
    public void getScore_returnsOneOn3VariantsWithCorrect() {
        decision.setAnswer("A1B2C3");

        assertThat(game.getScore(decision), is(1));
    }
}