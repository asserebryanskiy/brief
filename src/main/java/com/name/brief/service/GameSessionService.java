package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.model.GameSession;
import com.name.brief.web.dto.GameSessionDto;
import com.name.brief.web.dto.MoveToDto;

import java.time.LocalDate;
import java.util.List;

public interface GameSessionService {
    boolean isSessionActive(GameSession gameSession);
    void save(GameSession gameSession) throws GameSessionAlreadyExistsException;

    GameSession getSession(String strId, LocalDate activeDate);

    List<GameSession> getFutureSessions();

    List<GameSession> getPastSessions();

    GameSession getSession(Long gameSessionId);

    void changePhase(Long gameSessionId, int phaseNumber);

    /**
     * Is used to persist timer end time to a database, so if
     * page with timer reloads occasionally, timer position could be restored.
     * */
    void activateTimer(Long gameSessionId, String durationStr);

    void changeRound(Long gameSessionId, int nextRoundIndex);

    String getCorrectAnswerForCurrentRound(Long gameSessionId);

    void update(GameSessionDto dto);

    void delete(Long gameSessionId);

    void nullPlayersAnswers(Long gameSessionId);

    MoveToDto createMoveTo(Long gameSessionId);
}
