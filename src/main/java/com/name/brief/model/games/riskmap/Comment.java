package com.name.brief.model.games.riskmap;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(exclude = "votes")
public class Comment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    private String content;
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Vote> votes;
    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    public Comment() {
        super();
        votes = new ArrayList<>();
    }
}
