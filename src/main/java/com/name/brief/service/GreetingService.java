package com.name.brief.service;

import com.name.brief.model.games.conference.GreetingAnswer;

public interface GreetingService {
    Long addAnswer(Long conferenceId, GreetingAnswer greetingAnswer);

    void updateAnswer(Long greetingAnswerId, int imgIndex, String comment);

    GreetingAnswer getGreetingAnswer(Long conferenceId, Long participantId);
}
