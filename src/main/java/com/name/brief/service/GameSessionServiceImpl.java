package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.exception.GameSessionNotFoundException;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.utils.TimeConverter;
import com.name.brief.web.dto.GameSessionDto;
import com.name.brief.web.dto.MoveToDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class GameSessionServiceImpl implements GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionRepository = gameSessionRepository;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @Override
    public boolean isSessionActive(GameSession gameSession) {
        return gameSessionRepository.findByStrIdAndActiveDate(gameSession.getStrId(), LocalDate.now()) != null;
    }

    @Override
    public void save(GameSession gameSession) throws GameSessionAlreadyExistsException {
        GameSession saved = getSession(gameSession.getStrId(), gameSession.getActiveDate());
        if (saved != null) throw new GameSessionAlreadyExistsException();

        // is done here because game need to be saved before referencing from unsaved gameSession
        gameSession.getGame().setGameSession(gameSession);
        gameSessionRepository.save(gameSession);
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
    @Transactional
    public void changePhase(Long gameSessionId, int phaseNumber) {
        gameSessionRepository.changePhase(gameSessionId, phaseNumber);
    }

    @Override
    @Transactional
    public void activateTimer(Long gameSessionId, String durationStr) {
        gameSessionRepository.setEndOfTimer(gameSessionId, LocalTime.now()
                .plus(TimeConverter.getDurationFromTimeStr(durationStr)));
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

    @Override
    public void update(GameSessionDto dto) {
        GameSession current = gameSessionRepository.findOne(dto.getGameSessionId());
        if (current != null) {
            // update gameSession fields
            current.setStrId(dto.getNewStrId());
            current.setActiveDate(dto.getActiveDate());

            // update commands if needed
            List<Player> players = current.getPlayers();
            int currentSize = players.size();
            int newSize = dto.getNumberOfCommands();
            if (newSize < currentSize) {
                for (int i = newSize; i < currentSize; i++) {
                    players.get(newSize).setGameSession(null);
                    players.remove(newSize);
                }
            }
            if (newSize > currentSize) {
                for (int i = currentSize; i < newSize; i++) {
                    players.add(new Player(current, String.valueOf(i + 1)));
                }
            }

            // update players usernames
            players.forEach(player -> player.setUsername(Player.constructUsername(
                    current.getStrId(),
                    current.getActiveDate(),
                    player.getCommandName())
            ));

            gameSessionRepository.save(current);
        }
    }

    @Override
    public void delete(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findOne(gameSessionId);
        if (gameSession == null) throw new GameSessionNotFoundException();
        gameSession.getPlayers().forEach(playerAuthenticationService::logout);
        gameSessionRepository.delete(gameSessionId);
    }

    @Override
    public void nullPlayersAnswers(Long gameSessionId) {
        GameSession session = getSession(gameSessionId);
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer(null)));
        gameSessionRepository.save(session);
    }

    @Override
    public MoveToDto createMoveTo(Long gameSessionId) {
        MoveToDto dto = new MoveToDto();
        dto.setPhase(gameSessionRepository.findCurrentPhaseNumberById(gameSessionId).getCurrentPhaseNumber());
        dto.setRound(gameSessionRepository.findCurrentRoundIndexById(gameSessionId).getCurrentRoundIndex());
        return dto;
    }

    @Override
    public boolean isSessionActive(String strId, LocalDate date) {
        return gameSessionRepository.findByStrIdAndActiveDate(strId, date) != null;
    }
}
