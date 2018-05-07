package com.name.brief.service;

import com.name.brief.model.Player;
import com.name.brief.web.dto.PlayerConnectionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Component
public class PlayerAuthenticationEventListener implements ApplicationListener<ApplicationEvent> {

    private final SimpMessagingTemplate template;
    private GameSessionService gameSessionService;

    @Autowired
    public PlayerAuthenticationEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Autowired
    public void setGameSessionService(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectedEvent) {
            Object principal = ((Authentication) ((SessionConnectedEvent) event).getUser())
                    .getPrincipal();
            if (principal instanceof Player) {
                sendToClient(PlayerConnectionDto.PlayerConnectionInstruction.CONNECT, (Player) principal);
            }
        }
        if (event instanceof SessionDisconnectEvent) {
            Object principal = ((Authentication) ((SessionDisconnectEvent) event).getUser())
                    .getPrincipal();
            if (principal instanceof Player) {
                sendToClient(PlayerConnectionDto.PlayerConnectionInstruction.DISCONNECT, (Player) principal);
            }
        }
        if (event instanceof SessionDestroyedEvent) {
            List<SecurityContext> contexts = ((SessionDestroyedEvent) event).getSecurityContexts();
            if (!contexts.isEmpty()) {
                Object principal = contexts.get(0).getAuthentication().getPrincipal();
                if (principal instanceof Player) {
                    gameSessionService.removePlayer((Player) principal);
                }
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
