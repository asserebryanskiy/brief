package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.web.dto.GameSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService,
                                 PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @RequestMapping("/moderator/gameSession/{gameSessionId}")
    public String getGameSession(@PathVariable Long gameSessionId, Model model) {
        GameSession session = gameSessionService.getSession(gameSessionId);
        model.addAttribute("gameSession", session);
        model.addAttribute("dashboardView", true);
        model.addAttribute("loggedInPlayers",
                playerAuthenticationService.getAuthenticatedPlayersUsernames(gameSessionId));
        // comes from moderator controller if attempt to change session parameters failed
        if (!model.containsAttribute("changeGameSessionDto")) {
            model.addAttribute("changeGameSessionDto", GameSessionDto.createFrom(session));
        }
        String gameName = session.getGame().getEnglishName();

        return "administration/moderator/gamePanels/" + gameName;
    }

    @RequestMapping("/moderator/gameSession/{gameSessionId}/projector")
    public String getProjectorView(@PathVariable Long gameSessionId, Model model) {
        GameSession session = gameSessionService.getSession(gameSessionId);
        model.addAttribute("gameSession", session);
        model.addAttribute("projectorMode", true);
        return "game/" + session.getGame().getEnglishName();
    }

    @RequestMapping(value = "/moderator/gameSession/{gameSessionId}", method = RequestMethod.PUT)
    public String changeSessionSettings(@PathVariable Long gameSessionId) {

        return null;
    }
}
