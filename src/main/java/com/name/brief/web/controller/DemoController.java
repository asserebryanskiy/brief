package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.RiskMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoController {
    @RequestMapping({"/E86CD2557B8F9C99"})
    public String getRiskMapDemo(Model model) {
        model.addAttribute("gameSession", new GameSession.GameSessionBuilder("id")
                .withGame(new RiskMap()).build());
        model.addAttribute("decision", null);
        model.addAttribute("demo", true);
        return "game/riskMapDemo";
    }
}
