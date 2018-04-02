package com.name.brief.utils;

import com.name.brief.model.Decision;

import java.util.HashSet;
import java.util.Set;

public class BriefUtils {
    public static boolean[][] getAnswerMatrix(Decision decision) {
        String answer = decision.getAnswer();
        boolean[][] result = new boolean[5][5];
        if (answer == null) return result;
        for (int i = 0; i < answer.length(); i+=2) {
            int row = Character.getNumericValue(answer.charAt(i + 1));
            int col = (int) answer.charAt(i) - 65 + 1;  // 65 - code of 'A' in Unicode
            result[row][col] = true;
        }
        return result;
    }


    /**
     * Converts Decision object to a set of variants, that are represented by String like "B1".
     *
     * Example:
     *      Decision decision = new Decision();
     *      decision.setAnswer("A1B2C3");
     *      toVariantsSet(decision) = Set.of("A1", "B2", "C3");
     *
     * @param decision - decision object containing answer
     * @return Set of variants
     */
    public static Set<String> toVariantsSet(Decision decision) {
        Set<String> result = new HashSet<>();
        String answer = decision.getAnswer();
        System.out.println(answer);
        for (int i = 0; i < answer.length(); i+=2) {
            result.add(answer.substring(i, i + 2));
        }
        return result;
    }
}
