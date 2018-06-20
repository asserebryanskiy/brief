package com.name.brief.repository;

import com.name.brief.model.games.conference.GreetingAnswer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingAnswerRepository extends CrudRepository<GreetingAnswer, Long> {
}
