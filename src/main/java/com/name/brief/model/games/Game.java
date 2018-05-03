package com.name.brief.model.games;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(exclude = "gameSession")
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
    public abstract int getScore(Decision decision);
    public abstract int getNumberOfRounds();
    public abstract String getEnglishName();
}
