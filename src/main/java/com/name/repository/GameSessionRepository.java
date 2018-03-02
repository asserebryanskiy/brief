package com.name.repository;

import com.name.model.GameSession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Long> {

    GameSession findByStrIdAndActiveDate(String strId, LocalDate activeDate);

    @Query("select s from GameSession s where s.user.id=:#{principal.id} and s.activeDate >= :date")
    List<GameSession> findSessionsAfter(@Param("date") LocalDate date);

    @Query("select s from GameSession s where s.user.id=:#{principal.id} and s.activeDate < :date")
    List<GameSession> findSessionsBefore(@Param("date") LocalDate date);
}
