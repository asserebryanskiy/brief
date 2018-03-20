package com.name.brief.utils;

import com.name.brief.model.Decision;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BriefUtilsTest {
    private Decision decision;

    @Before
    public void setUp() throws Exception {
        decision = new Decision();
    }

    @Test
    public void onEmptyInputReturnsArray5x5() {
        decision.setAnswer("");

        boolean[][] result = BriefUtils.getAnswerMatrix(decision);

        assertThat(result, is(new boolean[5][5]));
    }

    @Test
    public void onOneAnswerInputMarksProperCellAsSelected() {
        decision.setAnswer("B1");

        boolean[][] result = BriefUtils.getAnswerMatrix(decision);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boolean expectedResult = false;
                if (i == 1 && j == 2) expectedResult = true;
                assertThat(result[i][j], is(expectedResult));
            }
        }
    }

    @Test
    public void onTwoAnswerInputMarksProperCellAsSelected() {
        decision.setAnswer("B1C4");

        boolean[][] result = BriefUtils.getAnswerMatrix(decision);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boolean expectedResult = false;
                if ((i == 1 && j == 2) || (i == 4 && j == 3)) expectedResult = true;
                assertThat(result[i][j], is(expectedResult));
            }
        }
    }

    @Test
    public void toVariantsSet_returnsEmptySetOnEmptyInput() {
        decision.setAnswer("");

        Set<String> result = BriefUtils.toVariantsSet(decision);

        assertThat(result, hasSize(0));
    }

    @Test
    public void toVariantsSet_returnsProperSetOnOneVariantInput() {
        decision.setAnswer("A2");

        Set<String> result = BriefUtils.toVariantsSet(decision);

        assertThat(result, hasSize(1));
        assertThat(result, contains("A2"));
    }

    @Test
    public void toVariantsSet_returnsProperSetOnThreeVariantsInput() {
        decision.setAnswer("A2B4C2");

        Set<String> result = BriefUtils.toVariantsSet(decision);

        assertThat(result, hasSize(3));
        assertThat(result, contains("A2", "B4", "C2"));
    }
}