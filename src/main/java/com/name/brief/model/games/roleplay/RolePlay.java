package com.name.brief.model.games.roleplay;

import com.name.brief.model.Decision;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.time.Duration;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "roleplay")
@Data
public class RolePlay extends Game {
    private final int numberOfRounds = 1;
    private final String russianName = "Ролевая игра";
    private final String englishName = "rolePLay";

    private final String[] strategies = {
            "Доктор - Медицинский представитель"
    };

    @ElementCollection
    private Map<Long, Integer> playersScoreMap;
    private int strategyNumber;

    public RolePlay() {
        super();
    }

    public RolePlay(int numberOfCommands) {
        this();
        this.playersScoreMap = new HashMap<>(numberOfCommands);
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<>(4);
        phases.addAll(Arrays.asList(
                new Phase("Формирование игры", false),
                new Phase("Подключение участников", false),
                new Phase("Распределение ролей", false),
                new Phase("Отправить инструкции", false),
                new Phase("Игра", true, Duration.ofSeconds(300)),
                new Phase("Оценка партнера", false),
                new Phase("Результаты", false),
                new Phase("Смена ролей", false),
                new Phase("Отправить инструкции", false),
                new Phase("Игра", true, Duration.ofSeconds(300)),
                new Phase("Оценка партнера", false),
                new Phase("Результаты", false),
                new Phase("Завершить игру", false)
        ));
        for (int i = 0; i < phases.size(); i++) {
            phases.get(i).setId(String.valueOf(i));
        }
        return phases;
    }

    @Override
    public List<Phase> getPhases(int roundNumber) {
        return null;
    }

    @Override
    public int getScore(Decision decision) {
        return 0;
    }

    @Override
    public String getCorrectAnswer(int numberOfRound) {
        return null;
    }

    @Override
    public Object getCorrectAnswers() {
        return null;
    }

    @Override
    public Object getAnswerInput(Decision decision) {
        return null;
    }

    public void setStrategyNumber(int strategyNumber) {
        this.strategyNumber = strategyNumber;
    }
}
