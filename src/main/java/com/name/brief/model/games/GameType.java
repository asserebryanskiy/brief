package com.name.brief.model.games;

import com.name.brief.model.games.Phase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GameType {
    BRIEF("Бриф", 5, Constants.briefPhases);

    private final String russianName;
    private final int numberOfRounds;
    private final List<Phase> phases;

    GameType(String russianName, int numberOfRounds, List<Phase> phases) {
        this.russianName = russianName;
        this.numberOfRounds = numberOfRounds;
        this.phases = phases;
    }

    public String getRussianName() {
        return russianName;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    private static class Constants {
        private static final List<Phase> briefPhases = briefPhases();

        private static List<Phase> briefPhases() {
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
    }
}
