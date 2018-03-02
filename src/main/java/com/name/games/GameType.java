package com.name.games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum GameType {
    BRIEF("Бриф", 5, Constants.briefPhases);

    private final String russianName;
    private final int numberOfStages;
    private final List<Phase> phases;

    GameType(String russianName, int numberOfStages, List<Phase> phases) {
        this.russianName = russianName;
        this.numberOfStages = numberOfStages;
        this.phases = phases;
    }

    public String getRussianName() {
        return russianName;
    }

    public int getNumberOfStages() {
        return numberOfStages;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    private static class Constants {
        private static final List<Phase> briefPhases = briefPhases();

        private static List<Phase> briefPhases() {
            List<Phase> phases = new ArrayList<>(6);
            phases.addAll(Arrays.asList(
                    new Phase(0, "Правила", false),
                    new Phase(1, "Постановка задачи", true, "0:15"),
                    new Phase(2, "Выдача полей", false),
                    new Phase(3, "Внесение ответов", true, "0:05"),
                    new Phase(4, "Правильный ответ", false),
                    new Phase(5, "Результаты команд", false),
                    new Phase(6, "Следующий раунд", false)
            ));
            return phases;
        }
    }
}
