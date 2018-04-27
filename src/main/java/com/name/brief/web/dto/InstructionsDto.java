package com.name.brief.web.dto;

import com.name.brief.model.games.roleplay.DoctorRole;
import com.name.brief.model.games.roleplay.PharmaRole;
import lombok.Data;
import org.springframework.messaging.core.MessagePostProcessor;

@Data
public class InstructionsDto {
    private String roleName;
    private String instruction;

    public InstructionsDto(Object role) {
        if (role instanceof PharmaRole) {
            this.roleName = ((PharmaRole) role).getRussianName();
            this.instruction = ((PharmaRole) role).getInstruction();
        } else if (role instanceof DoctorRole) {
            this.roleName = ((DoctorRole) role).getRussianName();
            this.instruction = ((DoctorRole) role).getInstruction();
        }
    }
}
