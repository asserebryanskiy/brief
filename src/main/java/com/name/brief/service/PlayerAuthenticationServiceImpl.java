package com.name.brief.service;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.web.dto.PlayerConnectionDto;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static com.name.brief.web.dto.PlayerConnectionDto.*;

@Service
public class PlayerAuthenticationServiceImpl implements PlayerAuthenticationService {

    private final SimpMessagingTemplate template;
    private SessionRegistry sessionRegistry;
    private GameSessionService gameSessionService;

    @Autowired
    public PlayerAuthenticationServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof Player
                        && isLoggedIn((Player) p)
                        && ((Player) p).getGameSession().getId().equals(gameSessionId))
                .map(p -> ((Player) p).getUsername())
                .collect(Collectors.toSet());
    }

    @Override
    public void logout(Player player) {
        sessionRegistry.getAllSessions(player, true)
                .forEach(sessionInformation -> {
                    if (!sessionInformation.isExpired()) {
                        sessionInformation.expireNow();
                    }
                    sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                });
        gameSessionService.removePlayer(player);

        // send to moderator info about player logout
        sendToClient(PlayerConnectionInstruction.LOGOUT, player);

        // send to player instruction (if he is still in game) to return to index page
        template.convertAndSend("/queue/" + player.getUsername() + "/logout", "");
    }

    @Override
    public boolean isLoggedIn(Player player) {
        return sessionRegistry.getAllSessions(
                player, false).size() > 0;
    }

    @Override
    public boolean isLoggedIn(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> ((Player) p).getUsername().equals(username) && isLoggedIn((Player) p))
                .findAny()
                .orElse(null) != null;
    }

    @Autowired
    public void setGameSessionService(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @Override
    public void setSessionRegistry(SessionRegistry registry) {
        this.sessionRegistry = registry;
    }

    private void sendToClient(PlayerConnectionDto.PlayerConnectionInstruction command, Player player) {
        Long gameSessionId = player.getGameSession().getId();
        String destination = "/queue/" + gameSessionId + "/connection";
        PlayerConnectionDto dto = new PlayerConnectionDto(command, player.getUsername());
        switch (player.getGameSession().getAuthenticationType()) {
            case COMMAND_NAME:
                dto.setIdentifierForModerator(player.getCommandName());
                break;
            default:
                dto.setIdentifierForModerator(String.valueOf(player.getId()));
        }
        template.convertAndSend(destination, dto);
    }
}
