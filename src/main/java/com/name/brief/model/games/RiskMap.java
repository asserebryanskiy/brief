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
public class RiskMap extends Game {

    private static final String DEFAULT_COMMENT = "На картинке нет опасных ситуаций";
    private final int numberOfRounds = 1;
    private final String russianName = "Карта рисков";
    private final String englishName = "riskMap";

    public RiskMap() {
        super();
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<>(4);
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
    public Object getCorrectAnswers() {
        return new int[]{
                -1,1,1,-1,
                3,1,1,1,
                2,1,1,-1
        };
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

    public int[] getPossibleScore(int answer) {
        switch (answer) {
            case 0: return new int[]{-100, 100, 50, 25, 0};
            case 1: return new int[]{-200, 50, 200, 100, 50};
            case 2: return new int[]{-300, 25, 100, 300, 150};
            case 3: return new int[]{-400, 0, 50, 150, 400};
            default: return new int[]{0, 0, 0, 0, 0};
        }
    }

    public String getSectorComment(int sectorIndex) {
        String[] comments = new String[]{
                DEFAULT_COMMENT,
                "Риск падения работника\n",
                "Девушка на высоких каблуках в длинных брюках спускается по лестнице, говорит по телефону, в другой руке несет документы\n ",
                DEFAULT_COMMENT,
                "Коробками и батареями отопления перекрыт доступ к аварийному выходу – групповой смертельный случай\n",
                "Использование тряпки на входе вместо коврика\n",
                "Провод в местах прохода сотрудников\n",
                "Ограничен доступ к пожарному крану\n",
                "Использование удлинителя рядом с кулером, возможность попадания воды и короткого замыкания\n",
                "Вентилятор без защитной сетки\n",
                "Надпись мокрый пол упала на пол\n",
                DEFAULT_COMMENT
        };

        return comments[sectorIndex];
    }
}
