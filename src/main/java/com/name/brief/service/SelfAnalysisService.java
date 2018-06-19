package com.name.brief.service;

import com.name.brief.model.games.conference.SelfAnalysis;

public interface SelfAnalysisService {
    SelfAnalysis add(Long conferenceId, SelfAnalysis obj);

    SelfAnalysis getFor(Long participantId);

    SelfAnalysis update(Long conferenceId, SelfAnalysis selfAnalysis);
}
