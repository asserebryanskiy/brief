package com.name.brief.model.games.roleplay;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class PlayerLocation {
    private int hospital;
    private int room;

    public PlayerLocation() {
    }

    public PlayerLocation(int hospital, int room) {
        this.hospital = hospital;
        this.room = room;
    }
}
