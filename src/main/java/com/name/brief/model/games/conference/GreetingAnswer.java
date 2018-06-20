package com.name.brief.model.games.conference;

import com.name.brief.model.BaseEntity;
import com.name.brief.model.games.Conference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(exclude = "conference")
public class GreetingAnswer extends BaseEntity {
    @ManyToOne
    private Conference conference;
    private Long participantId;
    private int imgIndex;
    private String comment;
}
