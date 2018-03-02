package com.name.model;

import com.name.games.GameType;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Entity
@Data
@ToString(exclude = {"players", "stages"})
//@ValidateDateRange(startDate = "activeDate", endDate = "endDate")
public class GameSession extends BaseEntity{
    @NotNull
    private String strId;
    @NotNull
    private LocalDate activeDate;
    @NotNull
    private GameType gameType;
    @Transient
    private int numberOfCommands;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<Player> players;
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    private List<Stage> stages;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private int currentPhase;

    public GameSession() {
        super();
    }

    public GameSession(String strId,
                       LocalDate activeDate,
                       GameType gameType,
                       int numberOfCommands,
                       User user) {
        this();
        this.strId = strId;
        this.activeDate = activeDate;
        this.gameType = gameType;
        players = new ArrayList<>(numberOfCommands);
        IntStream.range(0, numberOfCommands)
                .forEach(i -> players.add(new Player(this, String.valueOf((i + 1)))));
        int numberOfStages = gameType.getNumberOfStages();
        stages = new ArrayList<>(numberOfStages);
        IntStream.range(0, numberOfStages)
                .forEach(i -> {
                    Stage stage = new Stage(this, i + 1);
                    if (i == 0) stage.setActive(true);
                    if (i == 1) stage.setNext(true);
                    stages.add(stage);
                });
        this.user = user;
    }

    public static class GameSessionBuilder {
        private String strId;
        private LocalDate activeDate;
        private GameType gameType;
        private int numberOfCommands;
        private User user;

        public GameSessionBuilder(String strId) {
            this.strId = strId;
        }

        public GameSessionBuilder withActiveDate(LocalDate activeDate) {
            this.activeDate = activeDate;
            return this;
        }

        public GameSessionBuilder withGameType(GameType gameType) {
            this.gameType = gameType;
            return this;
        }

        public GameSessionBuilder withNumberOfCommands(int numberOfCommands) {
            this.numberOfCommands = numberOfCommands;
            return this;
        }

        public GameSessionBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public GameSession build() {
            return new GameSession(
                    this.strId,
                    this.activeDate == null ? LocalDate.now() : this.activeDate,
                    this.gameType == null ? GameType.BRIEF : this.gameType,
                    this.numberOfCommands == 0 ? 5 : numberOfCommands,
                    this.user == null ? (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                            : this.user
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameSession session = (GameSession) o;
        return Objects.equals(strId, session.strId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), strId);
    }
}
