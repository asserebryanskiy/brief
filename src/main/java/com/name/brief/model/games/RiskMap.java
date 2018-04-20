package com.name.brief.model.games;

import com.name.brief.model.Decision;
import com.name.brief.model.games.riskmap.RiskMapAnswerType;
import com.name.brief.model.games.riskmap.RiskMapType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import java.time.Duration;
import java.util.*;

import static com.name.brief.model.games.riskmap.RiskMapType.*;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "riskmap")
@Data
public class RiskMap extends Game {

    public static final String OFFICE_DEFAULT_COMMENT = "На картинке нет опасных ситуаций";
    public static final String HOTEL_DEFAULT_COMMENT = "";
    private final int numberOfRounds = 1;
    private final String russianName = "Карта рисков";
    private final String englishName = "riskMap";

    private RiskMapType type = OFFICE;

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
        return type.getCorrectAnswers();
    }

    /*@Override
    public Object getCorrectAnswers() {
        return new int[]{
                -1,1,1,-1,
                1,1,1,3,
                2,0,-1,-1
        };
    }*/

    @Override
    public Object getAnswerInput(Decision decision) {
        int[] input = new int[12];
        switch (type) {
            case OFFICE:
                Arrays.fill(input, -1);
                break;
            case HOTEL:
                Arrays.fill(input, 3);
                break;
            default:
                Arrays.fill(input, 0);
        }
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
        switch (type) {
            case OFFICE: {
                switch (answer) {
                    case 0:
                        return new int[]{-100, 100, 50, 25, 0};
                    case 1:
                        return new int[]{-200, 50, 200, 100, 50};
                    case 2:
                        return new int[]{-300, 25, 100, 300, 150};
                    case 3:
                        return new int[]{-400, 0, 50, 150, 400};
                    default:
                        return new int[]{0, 0, 0, 0, 0};
                }
            }
            case HOTEL: {
                switch (answer) {
                    case 0: return new int[]{-50, 30};
                    case 1: return new int[]{-25, 20};
                    case 2: return new int[]{-10, 10};
                    case 3: return new int[]{-5,-5};
                    case 4: return new int[]{-10, 10};
                    case 5: return new int[]{-25, 20};
                    case 6: return new int[]{-50, 30};
                }
            }
        }
        return null;
    }

    public String getSectorComment(int sectorIndex) {
        String[] comments = type.getAnswerComments();
        return comments[sectorIndex];
    }

    /*public String getSectorComment(int sectorIndex) {
        String[] comments = new String[]{
                OFFICE_DEFAULT_COMMENT,
                "Риск падения работника\n",
                "Девушка на высоких каблуках в длинных брюках спускается по лестнице, говорит по телефону, в другой руке несет документы\n ",
                OFFICE_DEFAULT_COMMENT,
                "Ограничен доступ к пожарному крану\n",
                "Использование тряпки на входе вместо коврика\n",
                "Провод в местах прохода сотрудников\n",
                "Коробками и батареями отопления перекрыт доступ к аварийному выходу – групповой смертельный случай\n",
                "Использование удлинителя рядом с кулером, возможность попадания воды и короткого замыкания\n",
                "Вентилятор без защитной сетки\n",
                OFFICE_DEFAULT_COMMENT,
                OFFICE_DEFAULT_COMMENT
        };

        return comments[sectorIndex];
    }*/

    /**
     * Creates map that contains css class names for every integer answer
     * depending on RiskMapAnswerInput type;
     *
     * @return HashMap, where key is integer representation of an answer
     * and value is css class name of the answer.
     */
    public Map<Integer, String> getAnswerClassNames() {
        Map<Integer, String> map = null;
        switch (type.getAnswerType()) {
            case FIVE_ITEMS_SCALE: {
                map = new HashMap<>(5);
                map.put(-1, "no-answer");
                map.put(0, "no-level");
                map.put(1, "low-level");
                map.put(2, "mid-level");
                map.put(3, "high-level");
                return map;
            }
            case SEVEN_ITEMS_SCALE:
                map = new HashMap<>(7);
                for (int i = 0; i < 7; i++) {
                    map.put(i, "level-" + i);
                }
                return map;
        }

        return map;
    }
}
