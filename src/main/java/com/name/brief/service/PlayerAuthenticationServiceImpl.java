package com.name.brief.service;

import com.name.brief.exception.PlayerAuthenticationFailedException;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayerAuthenticationServiceImpl implements PlayerAuthenticationService {

    private final SessionRegistry playerSessionRegistry;
    private GameSessionService gameSessionService;

    @Autowired
    public PlayerAuthenticationServiceImpl(SessionRegistry playerSessionRegistry) {
        this.playerSessionRegistry = playerSessionRegistry;
    }

    @Override
    public Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId) {
        return playerSessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof Player
                        && isLoggedIn((Player) p)
                        && ((Player) p).getGameSession().getId().equals(gameSessionId))
                .map(p -> ((Player) p).getUsername())
                .collect(Collectors.toSet());
    }

    @Override
    public void logout(Player player) {
        playerSessionRegistry.getAllSessions(player, false)
                .forEach(SessionInformation::expireNow);
    }

    @Override
    public boolean isLoggedIn(Player player) {
        return playerSessionRegistry.getAllSessions(player, false).size() > 0;
    }

    @Override
    public boolean isLoggedIn(String username) {
        return playerSessionRegistry.getAllPrincipals().stream()
                .filter(p -> ((Player) p).getUsername().equals(username) && isLoggedIn((Player) p))
                .findAny()
                .orElse(null) != null;
    }

    @Autowired
    public void setGameSessionService(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @Override
    public void authenticate(PlayerLoginDto dto, HttpServletRequest request) {
        GameSession session = gameSessionService.getSession(dto.getGameSessionStrId(), LocalDate.now());
        Player player = gameSessionService.addPlayer(dto, session);
        login(player, request);
    }

    private void login(Player player, HttpServletRequest request) {
        try {
            request.login(player.getUsername(), player.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
