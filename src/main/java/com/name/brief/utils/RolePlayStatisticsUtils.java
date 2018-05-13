package com.name.brief.utils;

import com.name.brief.model.games.roleplay.*;
import com.name.brief.web.dto.AverageStatisticsDto;
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

        return formatAverage(average);
    }

    private static String formatAverage(double average) {
        return String.format("%.2f", average);
    }

    public static int getPlayerAnswer(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        return data.getAnswersAsSalesman().get(answerType).getAnswersPerRound().get(roundIndex);
    }

    public static String getCorrectAnswerFormatted(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        if (answerType == SalesmanAnswerType.SELL_FORECAST)
            return String.valueOf(data.getAnswersAsSalesman().get(answerType)
                    .getCorrectAnswersPerRound().get(roundIndex));

        return String.valueOf(getCorrectAnswer(answerType, data, roundIndex));
    }

    private static int getCorrectAnswer(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        return data.getAnswersAsSalesman().get(answerType).getCorrectAnswersPerRound().get(roundIndex);
    }

    public static int getError(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        int playerAnswer = getPlayerAnswer(answerType, data, roundIndex);
        int correctAnswer = getCorrectAnswer(answerType, data, roundIndex);

        return Math.abs(playerAnswer - correctAnswer);
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
    public static String getCssClassOfError(SalesmanAnswerType answerType, PlayerData data, int roundIndex) {
        // depending on diff apply class
        return getErrorCssClass(getError(answerType, data, roundIndex));
    }

    public static int getAverageError(SalesmanAnswerType answerType, RolePlay game) {
        double average = game.getPlayersData().stream()
                .filter(data -> data.getRole() instanceof SalesmanRole)
                .mapToDouble(data -> {
                    int sumOfRoundAvg = 0;
                    int numberOfRounds = game.getNumberOfRounds();
                    for (int i = 0; i < numberOfRounds; i++) {
                        sumOfRoundAvg += getError(answerType, data, i);
                    }
                    return (double) sumOfRoundAvg / (double) numberOfRounds;
                }).average().orElse(0.0);

        return (int) Math.round(average);
    }

    public static String getAverageErrorCssClass(SalesmanAnswerType answerType, RolePlay game) {
        int avg = getAverageError(answerType, game);

        if (avg <= 1) return "very-low";
        if (avg == 2) return "low";
        if (avg == 3) return "mid";
        if (avg < 5) return "nearly-high";
        if (avg < 7) return "high";
        else return "very-high";
    }

    public static String getAverageAmongAllFormatted(SalesmanCompetency competency, RolePlay game) {
        double average = game.getPlayersData().stream()
                .filter(data -> data.getRole() instanceof SalesmanRole)
                .map(data -> data.getDoctorEstimation().get(competency))
                .flatMap(data -> data.getResults().stream())
                .mapToInt(i -> i)
                .average()
                .orElse(0.0);

        return formatAverage(average);
    }

    private static String getErrorCssClass(int diff) {
        if (diff == 1) return "low";
        if (diff == 2) return "mid";
        if (diff < 5) return "nearly-high";
        if (diff < 7) return "high";
        else return "very-high";
    }

    public static AverageStatisticsDto createAverageStatisticsDto(RolePlay game) {
        AverageStatisticsDto dto = new AverageStatisticsDto();

        Arrays.stream(SalesmanCompetency.values()).forEach(competency ->
                dto.getAverageCompetenciesResults().put(
                        competency.getCssClassName(),
                        getAverageAmongAllFormatted(competency, game))
        );

        Arrays.stream(SalesmanAnswerType.values()).forEach(answerType -> {
                dto.getAverageError().put(
                        answerType.getCssClassName(),
                        getAverageError(answerType, game));
                dto.getAverageErrorCssClass().put(
                        answerType.getCssClassName(),
                        getAverageErrorCssClass(answerType, game));
                }
        );

        return dto;
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
            List<Integer> error = new ArrayList<>(numberOfRounds);
            List<String> errorCssClass = new ArrayList<>(numberOfRounds);

            // add data for every round
            for (int i = 0; i < numberOfRounds; i++) {
                // add player's answer on this answerType in survey
                playerAnswers.add(getPlayerAnswer(answerType, playerData, i));

                // add correct answer
                correctAnswers.add(getCorrectAnswerFormatted(answerType, playerData, i));

                // add success percentage
                error.add(getError(answerType, playerData, i));

                // add success percentage result css class
                errorCssClass.add(getCssClassOfError(answerType, playerData, i));
            }

            // add to dto
            dto.getPlayerAnswersPerRound().put(answerType.getCssClassName(), playerAnswers);
            dto.getCorrectAnswersPerRound().put(answerType.getCssClassName(), correctAnswers);
            dto.getErrorPerRound().put(answerType.getCssClassName(), error);
            dto.getErrorCssClassPerRound().put(answerType.getCssClassName(), errorCssClass);
        });

        // add comments
        playerData.getComments().stream()
                .map(RolePlayComment::getText)
                .forEach(comment -> dto.getComments().add(comment));

        return dto;
    }
}
