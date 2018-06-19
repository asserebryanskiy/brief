package com.name.brief.repository;

import com.name.brief.model.games.conference.ConferenceParticipantData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceParticipantDataRepository extends CrudRepository<ConferenceParticipantData, Long> {
}
