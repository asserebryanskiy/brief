package com.name.brief.model.games.conference;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ConferenceParticipantData {
    @Id
    private Long participantId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BestPractice> bestPractices = new ArrayList<>();

    public ConferenceParticipantData(Long participantId) {
        this.participantId = participantId;
    }
}
