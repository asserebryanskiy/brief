package com.name.brief.web.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SalesmanStatisticsDto {

    /**
     * Key is cssClassName of some SalesmanCompetency, value is average in all rounds
     * on that competency of a particular player.
     */
    private Map<String, String> competenciesAverage = new HashMap<>();
    /**
     * Key is cssClassName of some SalesmanAnswerType, value is a list of particular player's
     * answers in several rounds. One list element = one round's answer.
     */
    private Map<String, List<Integer>> playerAnswersPerRound = new HashMap<>();
    /**
     * Key is cssClassName of some SalesmanAnswerType, value is a list of correct
     * answers in several rounds. One list element = one round's correct answer.
     */
    private Map<String, List<String>> correctAnswersPerRound = new HashMap<>();
    /**
     * Key is cssClassName of some SalesmanAnswerType, value is a list of success rates
     * in several rounds of a particular player. One list element = one round's correct answer.
     */
    private Map<String, List<Integer>> errorPerRound = new HashMap<>();
    private Map<String, List<String>> errorCssClassPerRound = new HashMap<>();
    private List<String> comments = new ArrayList<>();

    public SalesmanStatisticsDto() {
    }
}
