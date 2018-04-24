package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.exception.GameSessionNotFoundException;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.repository.PlayerRepository;
import com.name.brief.utils.TimeConverter;
import com.name.brief.web.dto.GameSessionDto;
import com.name.brief.web.dto.MoveToDto;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class GameSessionServiceImpl implements GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  PlayerRepository playerRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.playerRepository = playerRepository;
    }

    @Autowired
    public void setPlayerAuthenticationService(PlayerAuthenticationService playerAuthenticationService) {
        this.playerAuthenticationService = playerAuthenticationService;
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

            // update players usernames
            List<Player> players = current.getPlayers();
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
    public Player addPlayer(PlayerLoginDto dto, GameSession session) {
        // construct player
        Player player = new Player();
        player.setCommandName(dto.getCommandName());
        player.setName(dto.getName());
        player.setSurname(dto.getSurname());
        player.setGameSession(session);

        // update gameSession
        session.getPlayers().add(player);
        gameSessionRepository.save(session);

        // set players username
        if (session.getAuthenticationType() == AuthenticationType.COMMAND_NAME) {
            player.setUsername(Player.constructUsername(
                    session.getStrId(), session.getActiveDate(), player.getCommandName()));
        } else {
            //noinspection ConstantConditions - because we've added player four lines upper
            player.setUsername("player" + getLastPlayer(session).getId());
        }
        playerRepository.save(player);

        return player;
    }

    @Override
    public void removePlayer(Player player) {
        GameSession gameSession = gameSessionRepository.findOne(player.getGameSession().getId());
        gameSession.getPlayers().removeIf(p -> p.getId().equals(player.getId()));
        gameSessionRepository.save(gameSession);
    }

    private Player getLastPlayer(GameSession session) {
        return session.getPlayers().isEmpty() ? null
                : session.getPlayers().get(session.getPlayers().size() - 1);
    }
}
