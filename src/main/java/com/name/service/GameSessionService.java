package com.name.service;

import com.name.exception.GameSessionAlreadyExistsException;
import com.name.games.GameType;
import com.name.model.Player;
import com.name.model.GameSession;

import java.time.LocalDate;
import java.util.List;

public interface GameSessionService {
    boolean isSessionActive(GameSession gameSession);
    void save(GameSession gameSession) throws GameSessionAlreadyExistsException;

    GameSession getSession(String strId, LocalDate activeDate);

    List<GameSession> getFutureSessions();

    List<GameSession> getPastSessions();

    GameSession getSession(Long gameSessionId);

    GameType getGameType(Long gameSessionId);
}
