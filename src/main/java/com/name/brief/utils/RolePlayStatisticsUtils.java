package com.name.brief.utils;

import com.name.brief.model.games.roleplay.PlayerData;
import com.name.brief.model.games.roleplay.RolePlayComment;
import com.name.brief.model.games.roleplay.SalesmanAnswerType;
import com.name.brief.model.games.roleplay.SalesmanCompetency;
import com.name.brief.web.dto.SalesmanStatisticsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RolePlayStatisticsUtils {
    public static String getAverageOnCompetencyFormatted(SalesmanCompetency competency, PlayerData data) {
        double average = data.getDoctorEstimation().get(competency).getResults().stream()
                .mapToInt(i -> i)
                .average()
                .orElse(0.0);

        return String.format("%.2f", average);
    }

    public static int getPlayerAnswer(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        return data.getAnswersAsSalesman().get(answerType).getAnswersPerRound().get(roundIndex);
    }

    public static String getCorrectAnswerFormatted(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        if (answerType == SalesmanAnswerType.SELL_FORECAST)
            return String.valueOf(data.getAnswersAsSalesman().get(answerType)
                    .getCorrectAnswersPerRound().get(roundIndex));

        int[] correctAnswerBorders = getCorrectAnswerBorders(answerType, data, roundIndex);

        // if boundaries are equal there is no need to return range
        if (correctAnswerBorders[0] == correctAnswerBorders[1])
            return String.valueOf(correctAnswerBorders[0]);

        return String.format("%d-%d", correctAnswerBorders[0], correctAnswerBorders[1]);
    }

    private static int[] getCorrectAnswerBorders(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        int correctAnswer = data.getAnswersAsSalesman().get(answerType).getCorrectAnswersPerRound().get(roundIndex);
        int stDeviation = data.getAnswersAsSalesman().get(answerType).getStDeviationPerRound().get(roundIndex);
        int bottomBorder = (correctAnswer - stDeviation);
        int topBorder = (correctAnswer + stDeviation);

        return new int[]{bottomBorder, topBorder};
    }

    public static int getSuccessRate(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        int correctAnswer = getCorrectAnswer(answerType, data, roundIndex);
        int playerAnswer = getPlayerAnswer(answerType, data, roundIndex);
        if (playerAnswer == correctAnswer) {
            return 100;
        }

        // compute difference between correct answer and given one
        int diff = Math.abs(correctAnswer - playerAnswer);

        // compute and return the relation
        if (correctAnswer == 0 || diff == 0) {
            if (diff == 1) return 80;
            if (diff == 2) return 60;
            if (diff < 5) return 40;
            if (diff < 7) return 20;
            else return 0;
        } else {
            return 100 - Math.round(((float) diff / (float) correctAnswer) * 100);
        }
    }

    private static int getCorrectAnswer(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        return data.getAnswersAsSalesman().get(answerType).getCorrectAnswersPerRound().get(roundIndex);
    }

    /**
     * Returns top/high/nearly-high/medium/low/very-low depending on the success
     * percentage (how close he was to the correct answer in particular
     * question) of the player.
     *
     * Is used by thymeleaf processor in game/rolePlay template
     *
     * @param answerType - type of salesman answer
     * @param data - player data, that contains answers and correct answers
     * @return css class appropriate to player's error rate.
     */
    public static String getCssClassOfSuccessRate(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        int playerAnswer = getPlayerAnswer(answerType, data, roundIndex);
        int[] correctAnswerBoundaries = getCorrectAnswerBorders(answerType, data, roundIndex);

        // top level is one that lies in correct answer boundaries
        if (playerAnswer >= correctAnswerBoundaries[0]
                && playerAnswer <= correctAnswerBoundaries[1]) return "top";

        // compute difference from given answer and nearest boundary
        int diff;
        if (playerAnswer < correctAnswerBoundaries[0])
            diff = correctAnswerBoundaries[0] - playerAnswer;
        else diff = playerAnswer - correctAnswerBoundaries[1];

        // depending on diff apply class
        return getSuccessRateCssClass(diff);
    }

    private static String getSuccessRateCssClass(int diff) {
        if (diff == 1) return "high";
        if (diff == 2) return "nearly-high";
        if (diff < 5) return "mid";
        if (diff < 7) return "low";
        else return "very-low";
    }

    public static SalesmanStatisticsDto createSalesmanStatisticsDto(PlayerData playerData) {
        SalesmanStatisticsDto dto = new SalesmanStatisticsDto();
        final int numberOfRounds = playerData.getAnswersAsSalesman()
                .get(SalesmanAnswerType.SELL_FORECAST).getAnswersPerRound().size();

        // add partner's estimations of player's skills
        Arrays.stream(SalesmanCompetency.values()).forEach(competency ->
                dto.getCompetenciesAverage().put(
                        competency.getCssClassName(),
                        getAverageOnCompetencyFormatted(competency, playerData))
        );

        Arrays.stream(SalesmanAnswerType.values()).forEach(answerType -> {
            // initialize data structures
            List<Integer> playerAnswers = new ArrayList<>(numberOfRounds);
            List<String> correctAnswers = new ArrayList<>(numberOfRounds);
            List<Integer> successRate = new ArrayList<>(numberOfRounds);
            List<String> successRateCssClass = new ArrayList<>(numberOfRounds);

            // add data for every round
            for (int i = 0; i < numberOfRounds; i++) {
                // add player's answer on this answerType in survey
                playerAnswers.add(getPlayerAnswer(answerType, playerData, i));

                // add correct answer
                correctAnswers.add(getCorrectAnswerFormatted(answerType, playerData, i));

                // add success percentage
                successRate.add(getSuccessRate(answerType, playerData, i));

                // add success percentage result css class
                successRateCssClass.add(getCssClassOfSuccessRate(answerType, playerData, i));
            }

            // add to dto
            dto.getPlayerAnswersPerRound().put(answerType.getCssClassName(), playerAnswers);
            dto.getCorrectAnswersPerRound().put(answerType.getCssClassName(), correctAnswers);
            dto.getSuccessRatePerRound().put(answerType.getCssClassName(), successRate);
            dto.getSuccessRateCssClassPerRound().put(answerType.getCssClassName(), successRateCssClass);
        });

        // add comments
        playerData.getComments().stream()
                .map(RolePlayComment::getText)
                .forEach(comment -> dto.getComments().add(comment));

        return dto;
    }
}
