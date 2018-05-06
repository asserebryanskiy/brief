package com.name.brief.web.dto;

import com.name.brief.model.games.roleplay.GameRole;
import lombok.Data;

@Data
public class InstructionsDto {
    private String roleName;
    private String instruction;

    public InstructionsDto(GameRole role) {
        this.roleName = role.getRussianName();
        this.instruction = role.getInstruction();
    }
}
