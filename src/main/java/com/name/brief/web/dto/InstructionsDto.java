package com.name.brief.web.dto;

import com.name.brief.model.games.roleplay.GameRole;
import com.name.brief.model.games.roleplay.PharmaRole;
import lombok.Data;

@Data
public class InstructionsDto {
    private String englishRoleName;
    private String russianRoleName;
    private String instruction;

    public InstructionsDto(GameRole role) {
        this.englishRoleName = role.toString();
        this.russianRoleName = role.getRussianName();
        this.instruction = role.getInstruction();
    }
}