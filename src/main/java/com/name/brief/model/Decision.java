package com.name.brief.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
public class Decision extends BaseEntity {
    @ManyToOne
    private Player player;
    private int roundNumber;
    private String answer;

    public Decision() {
        super();
    }

    public Decision(Player player, int roundNumber, String answer) {
        this();
        this.player = player;
        this.roundNumber = roundNumber;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer != null && answer.isEmpty() ? "-" : answer;
    }
}
