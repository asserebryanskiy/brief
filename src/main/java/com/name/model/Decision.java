package com.name.model;

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
    @ManyToOne
    private Stage stage;
    @Pattern(regexp = "[A-D][0-4](\\s[A-D][0-4])*", message = "{decision.answer.pattern}")
    private String answer;

    public Decision() {
        super();
    }
}
