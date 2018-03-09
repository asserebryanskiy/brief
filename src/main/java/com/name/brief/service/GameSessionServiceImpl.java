package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.exception.GameSessionNotFoundException;
import com.name.brief.model.GameSession;
import com.name.brief.model.games.GameType;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.utils.TimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    @Transactional
    public void changePhase(Long gameSessionId, int phaseNumber) {
        gameSessionRepository.changePhase(gameSessionId, phaseNumber);
        /*GameSession gameSession = gameSessionRepository.findOne(gameSessionId);
        if (gameSession == null) throw new GameSessionNotFoundException();
        gameSession.setCurrentPhaseNumber(phaseNumber);
        gameSessionRepository.save(gameSession);*/
    }

    @Override
    @Transactional
    public void activateTimer(Long gameSessionId, String durationStr) {
        gameSessionRepository.setEndOfTimer(gameSessionId, LocalTime.now()
                .plus(TimeConverter.getDurationFromTimeStr(durationStr)));
        /*GameSession gameSession = gameSessionRepository.findOne(gameSessionId);
        if (gameSession == null) throw new GameSessionNotFoundException();
        gameSession.activateTimer(TimeConverter.getDurationFromTimeStr(durationStr));
        gameSessionRepository.save(gameSession);*/
    }

    @Override
    public void changeRound(Long gameSessionId, int nextRoundIndex) {
        GameSession gameSession = gameSessionRepository.findOne(gameSessionId);
        if (gameSession == null) throw new GameSessionNotFoundException();
        gameSession.setCurrentRoundIndex(nextRoundIndex);
        gameSession.setCurrentPhaseNumber(0);
        gameSession.setEndOfTimer(null);
        gameSessionRepository.save(gameSession);
    }

    @Override
    public String getCorrectAnswerForCurrentRound(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findOne(gameSessionId);
        if (gameSession == null) throw new GameSessionNotFoundException();
        return gameSession.getGame().getCorrectAnswer(gameSession.getCurrentRoundIndex());
    }
}
