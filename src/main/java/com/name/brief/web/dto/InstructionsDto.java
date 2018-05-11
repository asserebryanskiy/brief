package com.name.brief.web.dto;

import com.name.brief.model.games.roleplay.GameRole;
import com.name.brief.model.games.roleplay.PharmaRole;
import lombok.Data;

@Data
public class InstructionsDto {
    private String roleName;
    private String instruction;
    private String gameStartText;
    private String crossingText;

    public InstructionsDto(GameRole role) {
        this.roleName = role.getRussianName();
        this.instruction = role.getInstruction();
        if (role instanceof PharmaRole) {
            this.gameStartText = ((PharmaRole) role).getGameStartText();
            this.crossingText = ((PharmaRole) role).getCrossingText();
        }
    }
}
