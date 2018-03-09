package com.name.brief.model.games;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Decision;
import com.name.brief.utils.BriefUtils;

import javax.persistence.Entity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
public class Brief extends BaseEntity implements Game {

    private final int numberOfRounds = 5;
    private final String russianName = "Бриф";
    private final String englishName = "brief";
    private final String[] correctAnswers = new String[]{"A1", "A1", "A1", "A1", "A1"};

    public Brief() {
        super();
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<>(7);
        phases.addAll(Arrays.asList(
                new Phase("Правила", false),
                new Phase("Постановка задачи", true, Duration.ofSeconds(15)),
                new Phase("Выдача полей", false),
                new Phase("Внесение ответов", true, Duration.ofSeconds(30)),
                new Phase("Правильный ответ", false),
                new Phase("Результаты команд", false),
                new Phase("Следующий раунд", false)
        ));
        return phases;
    }

    @Override
    public int getScore(Decision decision) {
        if (decision == null || decision.getAnswer() == null)
            return 0;

        int roundIndex = decision.getRoundNumber()/* - 1*/;
        Set<String> variants = BriefUtils.toVariantsSet(decision);
        boolean hasCorrectAnswer = variants.contains(correctAnswers[roundIndex]);
        if (!hasCorrectAnswer) {
            return 0;
        } else {
            switch (variants.size()) {
                case 1:
                    return 3;
                case 2:
                    return 2;
                case 3:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    @Override
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    @Override
    public String getRussianName() {
        return russianName;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public String getCorrectAnswer(int numberOfRound) {
        return correctAnswers[numberOfRound];
    }

    @Override
    public String[] getCorrectAnswers() {
        return correctAnswers.clone();
    }

}