package com.name.brief.model.games.riskmap;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.games.RiskMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(exclude = {"comments", "stats"})
public class Sector extends BaseEntity implements Serializable{
    @ManyToOne
    @JoinColumn(name = "riskmap_id")
    private RiskMap riskMap;
    private int number;
    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL)
    private List<Comment> comments;
    private int[] stats;

    public Sector() {
        super();
    }

    public Sector(int number, RiskMap riskMap) {
        this();
        this.number = number;
        this.riskMap = riskMap;
        comments = new ArrayList<>();
        stats = new int[4];
    }
}
