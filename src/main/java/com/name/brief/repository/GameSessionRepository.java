package com.name.brief.repository;

import com.name.brief.model.GameSession;
import com.name.brief.repository.projections.CurrentPhaseOnly;
import com.name.brief.repository.projections.CurrentRoundOnly;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Long> {

    GameSession findByStrIdAndActiveDate(String strId, LocalDate activeDate);

    @Query("select s from GameSession s where s.user.id=:#{principal.id} and s.activeDate >= :date")
    List<GameSession> findSessionsAfter(@Param("date") LocalDate date);

    @Query("select s from GameSession s where s.user.id=:#{principal.id} and s.activeDate < :date")
    List<GameSession> findSessionsBefore(@Param("date") LocalDate date);

    CurrentPhaseOnly findCurrentPhaseNumberById(Long id);

    CurrentRoundOnly findCurrentRoundIndexById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update GameSession s set s.currentPhaseNumber = :phase where s.id = :id")
    void changePhase(@Param("id") Long gameSessionId, @Param("phase") int phaseNumber);

    @Modifying(clearAutomatically = true)
    @Query("update GameSession s set s.endOfTimer = :endOfTimer where s.id = :id")
    void setEndOfTimer(@Param("id") Long gameSessionId, @Param("endOfTimer") LocalTime endOfTimer);
}
