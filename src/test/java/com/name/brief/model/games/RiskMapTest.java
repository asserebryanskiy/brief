package com.name.brief.model.games;

import com.name.brief.model.Decision;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RiskMapTest {
    private Decision decision;
    private RiskMap riskMap;

    @Before
    public void setUp() throws Exception {
        decision = new Decision();
        riskMap = new RiskMap();
    }

    @Test
    public void getAnswerInput_returnsProperValueOnOneAnswerInput() {
        decision.setAnswer("0-2");

        int[] expected = new int[12];
        Arrays.fill(expected, -1);
        expected[0] = 2;

        assertThat(riskMap.getAnswerInput(decision), is(expected));
    }

    @Test
    public void getAnswerInput_returnsProperValueOnTwoAnswerInput() {
        decision.setAnswer("0-2,11-1");

        int[] expected = new int[12];
        Arrays.fill(expected, -1);
        expected[0] = 2;
        expected[11] = 1;

        assertThat(riskMap.getAnswerInput(decision), is(expected));
    }

    @Test
    public void getAnswerInput_returnsMinusOnesArrayOnEmptyInput() {
        int[] expected = new int[12];
        Arrays.fill(expected, -1);

        assertThat(riskMap.getAnswerInput(decision), is(expected));
    }
}