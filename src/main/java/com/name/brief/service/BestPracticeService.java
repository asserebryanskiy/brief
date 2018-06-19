package com.name.brief.service;

import com.name.brief.model.games.conference.BestPractice;

import java.util.List;

public interface BestPracticeService {
    BestPractice add(Long gameId, BestPractice bestPractice);
    BestPractice changeText(Long bestPracticeId, String text, Long conferenceId);

    List<BestPractice> getBestPracticesFor(Long participantId);

    List<BestPractice> getAllBestPractices(Long gameId);

    void delete(Long conferenceId, Long bestPracticeId);
}
