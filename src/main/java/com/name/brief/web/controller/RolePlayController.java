package com.name.brief.web.controller;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.games.Phase;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.RolePlayService;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller()
public class RolePlayController {

    private final RolePlayService rolePlayService;
    private final GameSessionService gameSessionService;

    @Autowired
    public RolePlayController(RolePlayService rolePlayService,
                              GameSessionService gameSessionService) {
        this.rolePlayService = rolePlayService;
        this.gameSessionService = gameSessionService;
    }

    @MessageMapping("/rolePlay/{gameId}/rolePlaySettings")
    public void setRolePlaySettings(@DestinationVariable Long gameId,
                                    RolePlaySettingsDto dto) throws WrongGameTypeException {
        rolePlayService.setUp(gameId, dto);
    }

    @MessageMapping("/rolePlay/{gameId}/phases")
    @SendTo("/queue/{gameId}/phases")
    public String[] getRolePLayPhases() {
        return new RolePlay().getPhases().stream()
                .map(Phase::getEnglishName)
                .toArray(String[]::new);
    }

    @MessageMapping("/rolePlay/{gameId}/changePhase")
    public void changePhase(int phaseIndex,
                            @DestinationVariable Long gameId) throws WrongGameTypeException, OddNumberOfPlayersException {
        rolePlayService.changePhase(phaseIndex, gameId);
    }
}
