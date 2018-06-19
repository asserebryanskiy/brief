package com.name.brief.repository;

import com.name.brief.model.games.conference.SelfAnalysis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelfAnalysisRepository extends CrudRepository<SelfAnalysis, Long> {
    SelfAnalysis findByParticipantId(Long participantId);
}
