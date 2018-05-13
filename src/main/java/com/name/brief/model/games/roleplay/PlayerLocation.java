package com.name.brief.model.games.roleplay;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class PlayerLocation {
    private int hospital;
    private int room;

    public PlayerLocation(int hospital, int room) {
        this.hospital = hospital;
        this.room = room;
    }
}
