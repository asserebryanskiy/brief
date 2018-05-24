package com.name.brief.web.controller;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.Player;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.RolePlayService;
import com.name.brief.web.dto.DoctorAnswerDto;
import com.name.brief.web.dto.DrugDistributionDto;
import com.name.brief.web.dto.RolePlaySettingsDto;
import com.name.brief.web.dto.SalesmanAnswerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller()
public class RolePlayController {

    private final RolePlayService rolePlayService;
    private final SimpMessagingTemplate template;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public RolePlayController(RolePlayService rolePlayService,
                              SimpMessagingTemplate template,
                              PlayerAuthenticationService playerAuthenticationService) {
        this.rolePlayService = rolePlayService;
        this.template = template;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @MessageMapping("/rolePlay/{gameId}/rolePlaySettings")
    public void setRolePlaySettings(@DestinationVariable Long gameId,
                                    RolePlaySettingsDto dto) throws WrongGameTypeException {
        rolePlayService.setUp(gameId, dto);
    }

    @MessageMapping("/rolePlay/{gameId}/changePhase")
    public void changePhase(int phaseIndex,
                            @DestinationVariable Long gameId) throws WrongGameTypeException, OddNumberOfPlayersException {
        rolePlayService.changePhase(phaseIndex, gameId);
    }

    @MessageMapping("/rolePlay/{gameId}/doctor/answer")
    public void saveDoctorAnswers(@DestinationVariable Long gameId,
                            DoctorAnswerDto dto,
                            Principal principal) throws WrongGameTypeException, OddNumberOfPlayersException {
        rolePlayService.saveDoctorAnswers(
                gameId,
                dto,
                ((Player) ((Authentication) principal).getPrincipal()).getId()
        );
    }

    @MessageMapping("/rolePlay/{gameId}/salesman/answer")
    public void saveSalesmanAnswers(@DestinationVariable Long gameId,
                            SalesmanAnswerDto dto,
                            Principal principal) throws WrongGameTypeException {
        rolePlayService.saveSalesmanAnswers(
                gameId,
                dto,
                ((Player) ((Authentication) principal).getPrincipal()).getId()
        );
    }

    @MessageMapping("/rolePlay/{gameId}/drugDistribution")
    public void saveDrugDistribution(@DestinationVariable Long gameId,
                                     DrugDistributionDto dto,
                                     Principal principal) throws WrongGameTypeException {
        rolePlayService.saveDrugDistribution(
                gameId,
                dto,
                ((Player) ((Authentication) principal).getPrincipal()).getId()
        );
    }

    @MessageMapping("/rolePlay/{gameId}/add30sec")
    public void add30secondsToTimer(@DestinationVariable Long gameId) {
        rolePlayService.add30sec(gameId);
    }

    @MessageMapping("/rolePlay/{gameId}/logoutPlayer")
    public void logoutPlayer(@DestinationVariable Long gameId, String username) {
        playerAuthenticationService.logout(username);
//        template.convertAndSend("/queue/rolePlay/player/" + id + "/logout", "");
    }
}
