package com.name.web.controller;

import com.name.model.Player;
import com.name.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class BriefController {

    private final SimpMessagingTemplate template;
    private final PlayerService playerService;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public BriefController(SimpMessagingTemplate template,
                           PlayerService playerService, SessionRegistry sessionRegistry) {
        this.template = template;
        this.playerService = playerService;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Is used by a players when he logs in. It notifies its moderator and changes its loggedIn field
     * to true.
    */
    @MessageMapping("/connect")
//    @SendTo("/queue/{gameSessionId}/connection")
    public void connectPlayer(Principal principal) {
        Player player = (Player) ((Authentication) principal).getPrincipal();
        playerService.login(player);
        template.convertAndSend("/queue/" + player.getGameSession().getId() + "/connection",
                "Connect " + player.getUsername());
    }


    /**
     * Is called from moderator panel (assets/js/administration/moderator/brief.js)
     * Deletes player session from session registry, redirects player to the main page
     * and changes its loggedIn field to false.
    * */
    @MessageMapping("/logout/{username}")
    public void logoutPlayer(@DestinationVariable String username) {
        Player player = (Player) sessionRegistry.getAllPrincipals().stream()
                .filter(p -> ((Player) p).getUsername().equals(username))
                .findAny()
                .orElse(null);

        if (player != null) {
            sessionRegistry.getAllSessions(player, false)
                    .forEach(SessionInformation::expireNow);
            playerService.logout(player);
            template.convertAndSend("/queue/" + player.getGameSession().getId() + "/connection",
                    "Logout " + player.getUsername());
            template.convertAndSend("/queue/" + username + "/logout", "");
        }
    }
}
