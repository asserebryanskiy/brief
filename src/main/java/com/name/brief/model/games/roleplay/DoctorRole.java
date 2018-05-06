package com.name.brief.model.games.roleplay;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
public enum DoctorRole implements GameRole {
    DOCTOR_1(
            "доктор Терапевт",
            "<p class=\"bold\">Кейс</p>Вы довольно часто назначаете для лечения и профилактики респираторных заболеваний травяные сборы, так как считаете, что естественные природные средства лучше воспринимаются организмом. При этом вы предпочитаете назначать смеси трав от другого производителя т.к. считаете, что их состав более стабилен и сбалансирован.\n" +
                    "<br><br>Среди ваших пациентов есть все возрастные категории, в среднем, к вам за неделю приходит 12-15 пациентов, которым показано применение сборов лекарственных трав. Как правило, вы рекомендуете смеси «простотрава». К сожалению, их довольно сложно найти в аптеках, к тому же они расфасованы по 100 гр., что не всегда удобно для пациентов, это может привести к срыву схемы лечения. Поэтому иногда (2-3 пациента в неделю), вы рекомендуете «Алтай-траву», которая доступна в большинстве аптека и расфасована в одноразовые пакетики. \n" +
                    "<br><br><p class=\"bold\">Задача</p>" +
                    "По окончанию визита, вам нужно будет оценить следующие компетенции МП: \n" +
                    "<br>- Знание продукта\n" +
                    "<br>- Выявление потребностей\n" +
                    "<br>- Навыки презентации\n" +
                    "<br>- Работа с возражениями\n" +
                    "<br><br>По окончанию всех визитов, пожалуйста, распределите имеющийся у вас запрос на 12 упаковок на следующий месяц между всеми МП, которые к вам приходили, в соответствии с их навыками: кто больше понравился – тому больше упаковок.\n",
            13,
            2,
            3,
            1
    ),
    DOCTOR_2(
            "доктор Терапевт",
            "<p class=\"bold\">Кейс</p>Среди ваших пациентов есть все возрастные категории, в среднем, к вам за неделю приходит 15-20 пациентов с респираторными заболеваниями. \n" +
                    "<br><br>При этом, особенно в период эпидемии, вы склонны назначать много антибиотиков, поскольку считаете, что именно так можно наиболее быстро остановить развитие заболевания. \n" +
                    "<br><br>В качестве поддерживающей терапии вы иногда (4-5 пациентов в неделю) рекомендуете наборы трав, хотя и не сильно верите в их эффективность. В рынке производителей трав вы не очень разбираетесь и не видите особой разницы между производителями.\n" +
                    "<br><br><p class=\"bold\">Задача</p>" +
                    "По окончанию визита, вам нужно будет оценить следующие компетенции МП:  \n" +
                    "<br>- Знание продукта\n" +
                    "<br>- Выявление потребностей\n" +
                    "<br>- Навыки презентации\n" +
                    "<br>- Работа с возражениями\n" +
                    "<br><br>По окончанию всех визитов, пожалуйста, распределите имеющийся у вас запрос на 12 упаковок на следующий месяц между всеми МП, которые к вам приходили, в соответствии с их навыками: кто больше понравился – тому больше упаковок\n.",
            17,
            2,
            0,
            0
    );

    private final String russianName;
    private final String instruction;
    private final int patientsAverage;
    private final int patientsStDeviation;
    private final int recipeAverage;
    private final int recipeStDeviation;

    DoctorRole(String russianName,
               String instruction,
               int patientsAverage,
               int patientsStDeviation,
               int recipeAverage,
               int recipeStDeviation) {
        this.russianName = russianName;
        this.instruction = instruction;
        this.patientsAverage = patientsAverage;
        this.patientsStDeviation = patientsStDeviation;
        this.recipeAverage = recipeAverage;
        this.recipeStDeviation = recipeStDeviation;
    }

    @Override
    public String getRussianName() {
        return russianName;
    }

    @Override
    public String getInstruction() {
        return instruction;
    }

    public int getPatientsAverage() {
        return patientsAverage;
    }

    public int getPatientsStDeviation() {
        return patientsStDeviation;
    }

    public int getRecipeAverage() {
        return recipeAverage;
    }

    public int getRecipeStDeviation() {
        return recipeStDeviation;
    }
}
