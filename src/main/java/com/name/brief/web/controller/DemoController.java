package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.RiskMap;
import com.name.brief.model.games.riskmap.RiskMapType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoController {
    @RequestMapping("/demo/E86CD2557B8F9C99")
    public String getOfficeRiskMapDemo(Model model) {
        prepare(model, RiskMapType.OFFICE);
        return "game/riskMapDemo";
    }

    @RequestMapping("/demo/515996F5F5433")
    public String getHotelRiskMapDemo(Model model) {
        prepare(model, RiskMapType.HOTEL);
        return "game/riskMapDemo";
    }

    private void prepare(Model model, RiskMapType type) {
        GameSession gameSession = new GameSession();
        RiskMap game = new RiskMap();
        game.setType(type);
        gameSession.setGame(game);
        model.addAttribute("gameSession", gameSession);
        model.addAttribute("decision", null);
        model.addAttribute("demo", true);
    }
}
