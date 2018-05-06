package com.name.brief.model.games.roleplay;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class RolePlayComment {

    @Column(name = "COMMENT_TEXT")
    private String text;

    public RolePlayComment() {
    }

    public RolePlayComment(String text) {
        this.text = text;
    }
}
