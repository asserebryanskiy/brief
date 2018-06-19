package com.name.brief.web.dto;

import com.name.brief.model.games.conference.BestPractice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestPracticeDto {
    private Long id;
    private Long participantId;
    private String text;

    public static BestPracticeDto createFrom(BestPractice bestPractice) {
        BestPracticeDto dto = new BestPracticeDto();
        dto.setId(bestPractice.getId());
        dto.setText(bestPractice.getText());
        dto.setParticipantId(bestPractice.getParticipantId());
        return dto;
    }

    public static List<BestPracticeDto> createListFrom(List<BestPractice> allBestPractices) {
        return allBestPractices.stream()
                .map(BestPracticeDto::createFrom)
                .collect(Collectors.toList());
    }

    public BestPractice createBestPractice() {
        BestPractice bestPractice = new BestPractice();
        bestPractice.setParticipantId(participantId);
        bestPractice.setText(text);
        return bestPractice;
    }
}
