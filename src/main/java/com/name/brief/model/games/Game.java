package com.name.brief.model.games;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public abstract class Game extends BaseEntity implements Serializable {
    @OneToOne
    private GameSession gameSession;

    public Game() {
        super();
    }

    public Game(GameSession gameSession) {
        this();
        this.gameSession = gameSession;
    }

    public abstract List<Phase> getPhases();
    public abstract List<Phase> getPhases(int roundNumber);
    public abstract int getScore(Decision decision);
    public abstract int getNumberOfRounds();
    public abstract String getRussianName();
    public abstract String getEnglishName();
    public abstract String getCorrectAnswer(int numberOfRound);
    public abstract String[] getCorrectAnswers();
    public abstract Object getAnswerInput(Decision decision);




}
