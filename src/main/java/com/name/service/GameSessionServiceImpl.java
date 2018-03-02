package com.name.service;

import com.name.exception.GameSessionAlreadyExistsException;
import com.name.games.GameType;
import com.name.model.Player;
import com.name.model.GameSession;
import com.name.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GameSessionServiceImpl implements GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final PlayerService playerService;

    @Autowired
    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  PlayerService playerService) {
        this.gameSessionRepository = gameSessionRepository;
        this.playerService = playerService;
    }

    @Override
    public boolean isSessionActive(GameSession gameSession) {
        return gameSessionRepository.findByStrIdAndActiveDate(gameSession.getStrId(), LocalDate.now()) != null;
    }

    @Override
    public void save(GameSession gameSession) throws GameSessionAlreadyExistsException {
        GameSession saved = getSession(gameSession.getStrId(), gameSession.getActiveDate());
        if (saved != null) throw new GameSessionAlreadyExistsException();
        gameSessionRepository.save(gameSession);
        gameSession.getPlayers().forEach(playerService::save);
    }

    @Override
    public GameSession getSession(String strId, LocalDate activeDate) {
        return gameSessionRepository.findByStrIdAndActiveDate(strId, activeDate);
    }

    @Override
    public List<GameSession> getFutureSessions() {
        return gameSessionRepository.findSessionsAfter(LocalDate.now());
    }

    @Override
    public List<GameSession> getPastSessions() {
        return gameSessionRepository.findSessionsBefore(LocalDate.now());
    }

    @Override
    public GameSession getSession(Long gameSessionId) {
        return gameSessionRepository.findOne(gameSessionId);
    }

    @Override
    public GameType getGameType(Long gameSessionId) {
        // ToDo: realize functionality
        return GameType.BRIEF;
    }
}
