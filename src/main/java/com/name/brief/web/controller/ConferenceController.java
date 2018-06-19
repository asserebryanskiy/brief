package com.name.brief.web.controller;

import com.name.brief.model.Player;
import com.name.brief.service.ConferenceService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@MessageMapping("/conference")
public class ConferenceController {
    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @MessageMapping("/{gameId}/changePhase")
    public void changePhase(@DestinationVariable Long gameId, int phaseIndex) {
        this.conferenceService.changePhase(gameId, phaseIndex);
    }
}
