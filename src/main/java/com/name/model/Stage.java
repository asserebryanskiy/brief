package com.name.model;

import com.name.games.brief.Round;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = "decisions")
public class Stage extends BaseEntity {
    @ManyToOne
    private GameSession gameSession;
    private int numberOfStage;
    private boolean active;
    private boolean played;
    private boolean passed;
    private boolean next;
    @OneToMany(mappedBy = "stage")
    private List<Decision> decisions;

    public Stage() {
        super();
        decisions = new ArrayList<>();
    }

    public Stage(GameSession gameSession, int numberOfStage) {
        this();
        this.gameSession = gameSession;
        this.numberOfStage = numberOfStage;
    }
}
