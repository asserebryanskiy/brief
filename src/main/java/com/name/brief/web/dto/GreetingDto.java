package com.name.brief.web.dto;

import com.name.brief.model.games.conference.GreetingAnswer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GreetingDto {
    private int imgIndex;
    private String comment;
    private Long greetingId;
    private Long conferenceId;
    private Long participantId;

    public static GreetingDto createFrom(GreetingAnswer greetingAnswer) {
        if (greetingAnswer == null) return null;

        GreetingDto dto = new GreetingDto();
        dto.setImgIndex(greetingAnswer.getImgIndex());
        dto.setComment(greetingAnswer.getComment());
        dto.setGreetingId(greetingAnswer.getId());
        return dto;
    }

    public GreetingAnswer createGreetingAnswer() {
        GreetingAnswer answer = new GreetingAnswer();
        answer.setParticipantId(participantId);
        answer.setImgIndex(imgIndex);
        answer.setComment(comment);
        return answer;
    }
}
