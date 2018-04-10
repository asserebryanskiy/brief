package com.name.brief.web.controller;

import com.name.brief.model.games.RiskMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoController {
    @RequestMapping({"/demo/riskMap", "/demo"})
    public String getRiskMapDemo(Model model) {
        model.addAttribute("game", new RiskMap());
        model.addAttribute("decision", null);
        model.addAttribute("demo", true);
        return "game/riskMapDemo";
    }
}
