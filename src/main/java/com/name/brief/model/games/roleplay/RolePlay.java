package com.name.brief.model.games.roleplay;

import com.name.brief.exception.NoPlayersAddedException;
import com.name.brief.model.Decision;
import com.name.brief.model.Player;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.Duration;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "roleplay")
@Data
public class RolePlay extends Game {
    @Transient
    private final int numberOfRounds = 1;
    @Transient
    private final String russianName = "Ролевая игра";
    @Transient
    private final String englishName = "rolePlay";
    @Transient
    public static final List<Phase> phases = formPhases();
    @Transient
    private final String[] strategies = {
            "Доктор - Медицинский представитель"
    };

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PlayerData> playersData = new ArrayList<>();
    private int strategyNumber;
    private int phaseIndex;
    private int roundIndex;

    public RolePlay() {
        super();
    }

    private static List<Phase> formPhases() {
        List<Phase> phases = new ArrayList<>(13);
        phases.addAll(Arrays.asList(
                new Phase("Формирование игры","FORM_GAME", false),
                new Phase("Подключение участников","CONNECT_PLAYERS", false),
                new Phase("Распределение ролей","SEND_ROLES", false),
                new Phase("Инструкция","SEND_INSTRUCTION", false),
                new Phase("Переход участников","CROSSING", false),
                new Phase("Игра", "GAME", true, Duration.ofSeconds(300)),
                new Phase("Анкета", "SURVEY", true, Duration.ofSeconds(120)),
                new Phase("Переход участников","CROSSING_2", false),
                new Phase("Игра", "GAME_2", true, Duration.ofSeconds(300)),
                new Phase("Анкета", "SURVEY_2", true, Duration.ofSeconds(120)),
                new Phase("Результаты", "RESULTS", false),
                new Phase("Обсуждение в командах", "DISCUSSION", false),
                new Phase("Смена ролей", "CHANGE_ROLES", false)
        ));
        for (int i = 0; i < phases.size(); i++) {
            phases.get(i).setOrderIndex(i);
        }

        return phases;
    }

    @Override
    public List<Phase> getPhases() {
        return phases;
    }

    @Override
    public int getScore(Decision decision) {
        return 0;
    }
}
