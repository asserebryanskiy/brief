package com.name.brief.model.games;

import com.name.brief.model.Decision;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "riskmap")
@Data
@ToString(exclude = "sectors")
public class RiskMap extends Game {

    private final int numberOfRounds = 1;
    private final String russianName = "Карта рисков";
    private final String englishName = "riskMap";

    public RiskMap() {
        super();
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<>(3);
        phases.addAll(Arrays.asList(
                new Phase("Объяснение правил", false),
                new Phase("Выбор ответа", true, Duration.ofSeconds(600)),
                new Phase("Результаты", false),
                new Phase("Начать заново", false)
        ));
        for (int i = 0; i < phases.size(); i++) {
            phases.get(i).setId(String.valueOf(i));
        }
        return phases;
    }

    @Override
    public List<Phase> getPhases(int roundNumber) {
        return getPhases();
    }

    @Override
    public int getScore(Decision decision) {
        return 0;
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
        return "A1";
    }

    @Override
    public String[] getCorrectAnswers() {
        return new String[0];
    }

    @Override
    public Object getAnswerInput(Decision decision) {
        int[] input = new int[12];
        Arrays.fill(input, -1);
        if (decision == null) return input;
        if (decision.getAnswer() != null) {
            String answer = decision.getAnswer();
            char[] chars = answer.toCharArray();
            boolean imgNumber = true;
            StringBuilder builder = new StringBuilder();
            for (char aChar : chars) {
                if (aChar == '-') {
                    imgNumber = false;
                } else if (aChar == ',') {
                    imgNumber = true;
                    builder.delete(0, builder.length());
                } else {
                    if (imgNumber) {
                        builder.append(aChar);
                    } else {
                        input[Integer.parseInt(builder.toString())]
                                = Character.getNumericValue(aChar);
                    }
                }
            }
        }
        return input;
    }
}
