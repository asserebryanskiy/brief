package com.name.brief.web.dto;

import com.name.brief.model.games.Phase;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.core.MessagePostProcessor;

@Data
@NoArgsConstructor
public class ChangePhaseDto {
    private String phaseName;
    private int phaseIndex;

    public ChangePhaseDto(Phase phase) {
        this.phaseName = phase.getEnglishName();
        this.phaseIndex = phase.getOrderIndex();
    }
}
