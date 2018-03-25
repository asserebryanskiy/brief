package com.name.brief.web.controller;

import com.name.brief.service.RiskMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RiskMapController {
    private final SimpMessagingTemplate template;
    private final RiskMapService riskMapService;

    @Autowired
    public RiskMapController(SimpMessagingTemplate template,
                             RiskMapService riskMapService) {
        this.template = template;
        this.riskMapService = riskMapService;
    }

    @MessageMapping("/{gameSessionId}/changeSector")
    public void changeSector(@DestinationVariable Long gameSessionId, String sectorNumber) {
        riskMapService.changeSectorNumber(sectorNumber, gameSessionId);
        template.convertAndSend("/topic/" + gameSessionId + "/changeSector", sectorNumber);
    }
}
