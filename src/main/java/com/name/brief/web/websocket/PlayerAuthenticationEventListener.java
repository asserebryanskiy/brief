package com.name.brief.web.websocket;

import com.name.brief.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class PlayerAuthenticationEventListener implements ApplicationListener<ApplicationEvent> {

    private final SimpMessagingTemplate template;

    @Autowired
    public PlayerAuthenticationEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectedEvent) {
            Object principal = ((Authentication) ((SessionConnectedEvent) event).getUser())
                    .getPrincipal();
            if (principal instanceof Player) {
                Long gameSessionId = ((Player) principal).getGameSession().getId();
                template.convertAndSend("/queue/" + gameSessionId + "/connection",
                        "Connect " + ((Player) principal).getUsername());
            }
        }
        if (event instanceof SessionDisconnectEvent) {
            Object principal = ((Authentication) ((SessionDisconnectEvent) event).getUser())
                    .getPrincipal();
            if (principal instanceof Player) {
                Long gameSessionId = ((Player) principal).getGameSession().getId();
                template.convertAndSend("/queue/" + gameSessionId + "/connection",
                        "Disconnect " + ((Player) principal).getUsername());
            }
        }
    }
}
