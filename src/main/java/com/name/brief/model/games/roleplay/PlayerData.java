package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
    private Set<Long> playedPlayers = new HashSet<>();
    private PlayerLocation location;
    @ElementCollection
    private List<RolePlayComment> comments = new ArrayList<>();
    private Long currentPartnerId;
    private PharmaRole role;

    public PlayerData(Player player) {
        this.player = player;
    }

    public PlayerData() {
        this(null);
    }
}
