package com.name.brief.model.games.roleplay;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class RolePlayComment {

    private String comment;

    public RolePlayComment() {
    }
}
