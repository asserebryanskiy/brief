package com.name.web.websocket;

import com.name.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class DisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private final SimpMessagingTemplate template;

    @Autowired
    public DisconnectEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Object principal = ((Authentication) event.getUser()).getPrincipal();
        if (principal instanceof Player) {
            Player player = (Player) principal;
            /*template.convertAndSend("/queue/" + player.getGameSession().getId() + "/connection",
                    "Disconnected " + player.getUsername());*/
        }
        System.out.println(event);
    }
}
