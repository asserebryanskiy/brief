package com.name.brief.service;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.conference.SelfAnalysis;

public interface ConferenceService {
    void changePhase(Long gameId, int phaseIndex);

    Conference getConference(Long conferenceId);

    void removeBestPractice(Long conferenceId, Long bestPracticeId);

    SelfAnalysis addSelfAnalysis(Long conferenceId, SelfAnalysis obj);

    void add30sec(Long gameId);
}
