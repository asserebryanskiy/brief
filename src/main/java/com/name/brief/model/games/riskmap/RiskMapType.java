package com.name.brief.model.games.riskmap;

import com.name.brief.model.games.RiskMap;
import lombok.Getter;

@Getter
public enum RiskMapType {
    OFFICE(
            "office",
            "Офис",
            new int[]{
                    -1,1,1,-1,
                    3,1,1,1,
                    2,1,1,-1
            },
            RiskMapAnswerType.FIVE_ITEMS_SCALE,
            new String[]{
                    RiskMap.DEFAULT_COMMENT,
                    "Риск падения работника\n",
                    "Девушка на высоких каблуках в длинных брюках спускается по лестнице, говорит по телефону, в другой руке несет документы\n ",
                    RiskMap.DEFAULT_COMMENT,
                    "Коробками и батареями отопления перекрыт доступ к аварийному выходу – групповой смертельный случай\n",
                    "Использование тряпки на входе вместо коврика\n",
                    "Провод в местах прохода сотрудников\n",
                    "Ограничен доступ к пожарному крану\n",
                    "Использование удлинителя рядом с кулером, возможность попадания воды и короткого замыкания\n",
                    "Вентилятор без защитной сетки\n",
                    "Надпись мокрый пол упала на пол",
                    RiskMap.DEFAULT_COMMENT
            }),
    HOTEL(
            "hotel",
            "Отель",
            new int[]{
                    0,0,0,0,
                    0,0,0,0,
                    0,0,0,0
            },
            RiskMapAnswerType.SEVEN_ITEMS_SCALE,
            new String[]{
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT,
                    RiskMap.DEFAULT_COMMENT
            }
    );

    private final String name;
    private final String russianName;
    private final int[] correctAnswers;
    private final RiskMapAnswerType answerType;
    private final String[] answerComments;

    RiskMapType(String name,
                String russianName,
                int[] correctAnswers,
                RiskMapAnswerType answerType,
                String[] answerComments) {
        this.name = name;
        this.correctAnswers = correctAnswers;
        this.answerType = answerType;
        this.answerComments = answerComments;
        this.russianName = russianName;
    }
}
