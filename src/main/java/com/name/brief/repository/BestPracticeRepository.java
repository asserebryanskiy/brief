package com.name.brief.repository;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.conference.BestPractice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BestPracticeRepository extends CrudRepository<BestPractice, Long> {
    List<BestPractice> findByParticipantId(Long participantId);

    List<BestPractice> findByConference(Conference conference);
}
