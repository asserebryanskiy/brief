package com.name.brief.web.dto;

import com.name.brief.model.games.conference.SelfAnalysis;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SelfAnalysisDto {
    private Long id;
    private Long participantId;
    private List<String> answers;
    private boolean readyToShare;

    public static SelfAnalysisDto createFrom(SelfAnalysis selfAnalysis) {
        if (selfAnalysis == null) return null;

        SelfAnalysisDto dto = new SelfAnalysisDto();

        dto.setId(selfAnalysis.getId());
        dto.setParticipantId(selfAnalysis.getParticipantId());
        dto.setAnswers(selfAnalysis.getAnswers());
        dto.setReadyToShare(selfAnalysis.isReadyToShare());

        return dto;
    }

    public SelfAnalysis createSelfAnalysis() {
        SelfAnalysis selfAnalysis = new SelfAnalysis();

        if (id != null) selfAnalysis.setId(getId());
        selfAnalysis.setParticipantId(getParticipantId());
        selfAnalysis.setAnswers(getAnswers());
        selfAnalysis.setReadyToShare(isReadyToShare());

        return selfAnalysis;
    }
}
