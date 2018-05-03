package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PlayerData extends BaseEntity {

    @NotNull
    @ManyToOne
    private Player player;
    private int score;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Long> playedPlayers = new HashSet<>();
    @Embedded
    private PlayerLocation location;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RolePlayComment> comments = new ArrayList<>();
    private Long currentPartnerId;
    @Enumerated
    private PharmaRole role;

    public PlayerData(Player player) {
        this.player = player;
    }

    public PlayerData() {
        this(null);
    }
}
