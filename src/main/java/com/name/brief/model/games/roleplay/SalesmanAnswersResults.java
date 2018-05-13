package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "answersPerRound")
public class SalesmanAnswersResults extends BaseEntity {

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> answersPerRound;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> correctAnswersPerRound;

    public SalesmanAnswersResults(int numberOfRounds) {
        super();
        this.answersPerRound = new ArrayList<>(numberOfRounds);
        for (int i = 0; i < numberOfRounds; i++) {
            answersPerRound.add(0);
        }

        this.correctAnswersPerRound = new ArrayList<>(numberOfRounds);
        for (int i = 0; i < numberOfRounds; i++) {
            correctAnswersPerRound.add(0);
        }
    }

}
