package com.name.brief.model.games.riskmap;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Vote extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Vote() {
        super();
    }
}
