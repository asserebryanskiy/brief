package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.RiskMap;
import com.name.brief.service.*;
import com.name.brief.web.dto.NextPhaseMessage;
import com.name.brief.web.dto.Answers;
import com.name.brief.web.dto.StatsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WsController {

    private final PlayerService playerService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public WsController(PlayerService playerService,
                        PlayerAuthenticationService playerAuthenticationService) {
        this.playerService = playerService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    /**
     * Is called from moderator panel (assets/js/administration/moderator/brief.js)
     * Deletes player session from session registry, redirects player to the main page
     * and changes its loggedIn field to false.
    * */
    @MessageMapping("/logout/{username}")
    public void logoutPlayer(@DestinationVariable String username) {
        Player player = playerService.findByUsername(username);

        if (player != null) {
            playerAuthenticationService.logout(player);
        }
    }
}
