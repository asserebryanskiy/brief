package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.access.method.P;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(exclude = {"player", "playedPlayers", "comments"})
public class PlayerData extends BaseEntity {

    @ManyToOne
    private Player player;

    private int orderNumber;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Long> playedPlayers = new HashSet<>();
    @Embedded
    private PlayerLocation location = new PlayerLocation();

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<SalesmanCompetency, CompetencyResults> doctorEstimation;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<SalesmanAnswerType, SalesmanAnswersResults> answersAsSalesman;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RolePlayComment> comments;
    private Long currentPartnerId;
    private String role;

    public PlayerData(Player player) {
        this.player = player;

        int numberOfRounds = 2;

        doctorEstimation = new HashMap<>();
        for (SalesmanCompetency competency : SalesmanCompetency.values()) {
            doctorEstimation.put(competency, new CompetencyResults(numberOfRounds));
        }

        answersAsSalesman = new HashMap<>();
        for (SalesmanAnswerType answer : SalesmanAnswerType.values()) {
            answersAsSalesman.put(answer, new SalesmanAnswersResults(numberOfRounds));
        }

        comments = new ArrayList<>(numberOfRounds);
        for (int i = 0; i < numberOfRounds; i++) {
            comments.add(new RolePlayComment(""));
        }
    }

    public PlayerData() {
        this(null);
    }

    public GameRole getRole() {
        try {
            return DoctorRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            return SalesmanRole.valueOf(role);
        }
    }

    public void setRole(GameRole role) {
        this.role = role.toString();
    }
}
