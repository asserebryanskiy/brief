package com.name.brief.model.games;

import com.name.brief.model.Decision;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.List;

@MappedSuperclass
public interface Game extends Serializable {
    List<Phase> getPhases();
    int getScore(Decision decision);
    int getNumberOfRounds();
    String getRussianName();
    String getEnglishName();
    String getCorrectAnswer(int numberOfRound);
    String[] getCorrectAnswers();
    Object getAnswerInput(Decision decision);
}
