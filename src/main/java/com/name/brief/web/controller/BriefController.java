package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.PlayerService;
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
public class BriefController {

    private final SimpMessagingTemplate template;
    private final PlayerService playerService;
    private final PlayerAuthenticationService playerAuthenticationService;
    private final GameSessionService gameSessionService;

    @Autowired
    public BriefController(SimpMessagingTemplate template,
                           PlayerService playerService,
                           PlayerAuthenticationService playerAuthenticationService,
                           GameSessionService gameSessionService) {
        this.template = template;
        this.playerService = playerService;
        this.playerAuthenticationService = playerAuthenticationService;
        this.gameSessionService = gameSessionService;
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
            template.convertAndSend("/queue/" + username + "/logout", "");
        }
    }

    /**
     * Send by moderator to make all connected players to change its phase.
     *
     * @param message containing number of next phase and timer str if needed.
     * @param gameSessionId - id of gameSession whose players should be pushed to next phase.
     * @return message of type NextPhaseMessage
     */
    @MessageMapping("/{gameSessionId}/changePhase")
    @SendTo("/topic/{gameSessionId}/changePhase")
    public NextPhaseMessage nextPhase(NextPhaseMessage message,
                                      @DestinationVariable Long gameSessionId) {
        gameSessionService.changePhase(gameSessionId, message.getPhaseNumber());

        // 4 is number of send correct responses phase
        if (message.getPhaseNumber() == 4) {
            message.setAdditional(gameSessionService.getCorrectAnswerForCurrentRound(gameSessionId));
        }
        return message;
    }

    @MessageMapping("/{gameSessionId}/changeRound")
    @SendTo("/topic/{gameSessionId}/changeRound")
    public int nextRound(int nextRoundIndex, @DestinationVariable Long gameSessionId) {
        gameSessionService.changeRound(gameSessionId, nextRoundIndex);
        return nextRoundIndex;
    }

    @MessageMapping("/{gameSessionId}/startTimer")
    public void startTimer(String durationStr, @DestinationVariable Long gameSessionId) {
        gameSessionService.activateTimer(gameSessionId, durationStr);
    }

    @MessageMapping("/responses")
    public void persistResponses(Answers answers, Principal principal) {
        Player player = (Player) ((Authentication) principal).getPrincipal();
        int roundIndex = gameSessionService.getSession(player.getGameSession().getId())
                .getCurrentRoundIndex();
        playerService.addResponses(player, answers.getAnswerStr(), roundIndex);
        answers.setScore(player.getScoreForRound(roundIndex));
        template.convertAndSend("/queue/" + player.getGameSession().getId() + "/answer",
                answers);
    }

    @MessageMapping("/{gameSessionId}/sendStatistics")
    @SendTo("/topic/{gameSessionId}/statistics")
    public StatsList sendStatistics(@DestinationVariable Long gameSessionId) {
        GameSession session = gameSessionService.getSession(gameSessionId);
        return session.getStatsList();
    }
}
