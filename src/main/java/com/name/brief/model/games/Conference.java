package com.name.brief.model.games;

import com.name.brief.model.Decision;
import com.name.brief.model.games.conference.BestPractice;
import com.name.brief.model.games.conference.GreetingAnswer;
import com.name.brief.model.games.conference.SelfAnalysis;
import com.name.brief.model.games.riskmap.RiskMapType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"bestPractices", "selfAnalyses", "greetingAnswers"})
public class Conference extends Game {
    @Transient
    private final String russianName = "Конференция";
    @Transient
    private final String englishName = "conference";
    @Transient
    public static final List<Phase> phases = formPhases();

    private int phaseIndex;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conference")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BestPractice> bestPractices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conference")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SelfAnalysis> selfAnalyses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conference")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<GreetingAnswer> greetingAnswers = new ArrayList<>();

    private static List<Phase> formPhases() {
        List<Phase> phases = new ArrayList<>(Arrays.asList(
            new Phase("Подключение участников", "CONNECT_PLAYERS", false),
            new Phase("Знакомство", "GREETING", true, Duration.ofMinutes(8)),
            new Phase("Заплатка", "PLACEHOLDER", false),
//            new Phase("Карта рисков", "RISK_MAP", true, Duration.ofMinutes(10)),
//            new Phase("Результаты", "RISK_MAP_RESULTS", false),
            new Phase("Лучшие практики", "BEST_PRACTICES", true, Duration.ofMinutes(5)),
            new Phase("Заплатка", "PLACEHOLDER", false),
//            new Phase("Заплатка", "PLACEHOLDER", false),
//            new Phase("Голосование", "BEST_PRACTICES_VOTING", false),
            new Phase("Заподырки", "SELF_ANALYSIS", true, Duration.ofMinutes(5)),
            new Phase("Заплатка", "PLACEHOLDER", false),
            new Phase("Завершение", "FINAL", false),
            new Phase("Еще раз", "AGAIN", false)
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

    @Override
    public int getNumberOfRounds() {
        return 0;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    public int[] getRiskMapPossibleScore(int answer) {
        RiskMap game = new RiskMap();
        game.setType(RiskMapType.OFFICE);
        return game.getPossibleScore(answer);
    }

    public int[] getRiskMapCorrectAnswers() {
        return new int[]{
            -1,1,1,-1,
            3,1,1,1,
            2,1,1,-1
        };
    }

    public String getPhaseName() {
        return phases.get(phaseIndex).getEnglishName();
    }
}
