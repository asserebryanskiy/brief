package com.name.brief.web.controller;

import com.name.brief.service.ConferenceService;
import com.name.brief.service.PlayerAuthenticationService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/conference")
public class ConferenceController {
    private final ConferenceService conferenceService;
    private final PlayerAuthenticationService playerAuthenticationService;

    public ConferenceController(ConferenceService conferenceService,
                                PlayerAuthenticationService playerAuthenticationService) {
        this.conferenceService = conferenceService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @MessageMapping("/{gameId}/changePhase")
    public void changePhase(@DestinationVariable Long gameId, int phaseIndex) {
        this.conferenceService.changePhase(gameId, phaseIndex);
    }

    @MessageMapping("/rolePlay/{gameId}/add30sec")
    public void add30secondsToTimer(@DestinationVariable Long gameId) {
        conferenceService.add30sec(gameId);
    }

    @MessageMapping("/rolePlay/{gameId}/logoutPlayer")
    public void logoutPlayer(@DestinationVariable Long gameId, String username) {
        playerAuthenticationService.logout(username);
//        template.convertAndSend("/queue/rolePlay/player/" + id + "/logout", "");
    }
}
