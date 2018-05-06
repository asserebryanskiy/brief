package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
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
@ToString(exclude = "results")
public class CompetencyResults extends BaseEntity {

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> results;

    public CompetencyResults(int numberOfRounds) {
        super();
        this.results = new ArrayList<>(numberOfRounds);
        for (int i = 0; i < numberOfRounds; i++) {
            results.add(0);
        }
    }

    public CompetencyResults() {
        this(0);
    }

}
