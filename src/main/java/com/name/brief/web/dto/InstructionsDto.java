package com.name.brief.web.dto;

import com.name.brief.model.games.roleplay.PharmaRole;
import lombok.Data;

@Data
public class InstructionsDto {
    private String roleName;
    private String instruction;

    public InstructionsDto(PharmaRole role) {
        this.roleName = role.getRussianName();
        this.instruction = role.getInstruction();
    }
}
