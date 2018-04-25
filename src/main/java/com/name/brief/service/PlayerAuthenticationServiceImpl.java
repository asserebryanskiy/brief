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

    private final SessionRegistry playerSessionRegistry;
    private final SimpMessagingTemplate template;
    private GameSessionService gameSessionService;

    @Autowired
    public PlayerAuthenticationServiceImpl(SessionRegistry playerSessionRegistry,
                                           SimpMessagingTemplate template) {
        this.playerSessionRegistry = playerSessionRegistry;
        this.template = template;
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
        playerSessionRegistry.getAllSessions(player, true)
                .forEach(sessionInformation -> {
                    if (!sessionInformation.isExpired()) {
                        sessionInformation.expireNow();
                    }
                    playerSessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                });
        gameSessionService.removePlayer(player);
        sendToClient(PlayerConnectionInstruction.LOGOUT, player);
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
//        playerSessionRegistry.registerNewSession(request.getSession().getId(), player);
    }

    @Component
    public class PlayerAuthenticationEventListener implements ApplicationListener<ApplicationEvent> {

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof SessionConnectedEvent) {
                Object principal = ((Authentication) ((SessionConnectedEvent) event).getUser())
                        .getPrincipal();
                if (principal instanceof Player) {
                    sendToClient(PlayerConnectionInstruction.CONNECT, (Player) principal);
                }
            }
            if (event instanceof SessionDisconnectEvent) {
                Object principal = ((Authentication) ((SessionDisconnectEvent) event).getUser())
                        .getPrincipal();
                if (principal instanceof Player) {
                    sendToClient(PlayerConnectionInstruction.DISCONNECT, (Player) principal);
                }
            }
            /*if (event instanceof AuthenticationSuccessEvent) {
                Authentication authentication = ((AuthenticationSuccessEvent) event).getAuthentication();
                Object principal = authentication == null ? null : authentication.getPrincipal();
                if (principal instanceof Player) {
                    Long gameSessionId = ((Player) principal).getGameSession().getId();
                    template.convertAndSend("/queue/" + gameSessionId + "/connection",
                            "Login " + ((Player) principal).getUsername());
                }
            }*/
        }
    }

    private void sendToClient(PlayerConnectionInstruction command, Player player) {
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

    private void login(Player player, HttpServletRequest request) {
        try {
            request.login(player.getUsername(), player.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
