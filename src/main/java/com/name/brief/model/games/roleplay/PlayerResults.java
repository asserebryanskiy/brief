package com.name.brief.model.games.roleplay;

import lombok.Data;

import javax.persistence.ElementCollection;
import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerResults {
    @ElementCollection
    private Map<String, CompetencyResults> doctorEstimation;
    @ElementCollection
    private Map<String, CompetencyResults> playerAnswers;

    public PlayerResults() {
        int numberOfRounds = 2;

        doctorEstimation = new HashMap<>();
        for (SalesmanCompetency competency : SalesmanCompetency.values()) {
            doctorEstimation.put(competency.getCssClassName(), new CompetencyResults(numberOfRounds));
        }

        playerAnswers = new HashMap<>();
        for (SalesmanAnswerType question : SalesmanAnswerType.values()) {
            playerAnswers.put(question.getCssClassName(), new CompetencyResults(numberOfRounds));
        }
    }
}
