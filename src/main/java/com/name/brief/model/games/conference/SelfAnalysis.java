package com.name.brief.model.games.conference;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.games.Conference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(exclude = "conference")
public class SelfAnalysis extends BaseEntity {

    @ManyToOne
    private Conference conference;
    private Long participantId;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> answers;
    private boolean readyToShare;

    public SelfAnalysis() {
        super();
    }
}
