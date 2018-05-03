package com.name.brief.model.games;

import com.name.brief.model.Decision;
import com.name.brief.utils.BriefUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Brief extends Game {

    private final int numberOfRounds = 5;
    private final String russianName = "Бриф";
    private final String englishName = "brief";
    private final String[] correctAnswers = new String[]{"A3", "A1", "B1", "D1", "D3"};

    public Brief() {
        super();
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<>(7);
        phases.addAll(Arrays.asList(
                new Phase("Правила", "RULES", false),
                new Phase("Постановка задачи", "SET_TASK", true, Duration.ofSeconds(90)),
                new Phase("Выдача полей","ISSUE_FIELDS",  false),
                new Phase("Внесение ответов","SEND_ANSWERS",  true, Duration.ofSeconds(90)),
                new Phase("Правильный ответ","CORRECT_ANSWER",  false),
                new Phase("Результаты команд", "RESULTS", false),
                new Phase("Следующий раунд","NEXT_ROUND",  false)
        ));
        for (int i = 0; i < phases.size(); i++) {
            phases.get(i).setOrderIndex(i);
        }
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
            int numberOfVariants = variants.size();
            if      (numberOfVariants == 1) return 15;
            else if (numberOfVariants == 2) return 10;
            else if (numberOfVariants < 5)  return 5;
            else if (numberOfVariants < 9)  return 2;
            else                            return 0;
        }
    }

    @Override
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    public String[] getCorrectAnswers() {
        return correctAnswers.clone();
    }
}