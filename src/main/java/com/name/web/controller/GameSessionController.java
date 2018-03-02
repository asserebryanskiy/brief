package com.name.web.controller;

import com.name.model.GameSession;
import com.name.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @RequestMapping("/moderator/gameSession/{gameSessionId}")
    public String getGameSession(@PathVariable Long gameSessionId, Model model) {
        GameSession session = gameSessionService.getSession(gameSessionId);
        model.addAttribute("gameSession", session);
        model.addAttribute("dashboardView", true);
        String gameType = session.getGameType().name().toLowerCase();

        return "administration/moderator/gamePanels/" + gameType;
    }

    @RequestMapping(value = "/moderator/gameSession/{gameSessionId}", method = RequestMethod.PUT)
    public String changeSessionSettings(@PathVariable Long gameSessionId) {

        return null;
    }

    @RequestMapping("/moderator/gameSession/{gameSessionId}/nextStage")
    public String startNewStage(@PathVariable String gameSessionId) {
        return null;
    }
}
