package com.name.web.controller;

import com.name.model.Player;
import com.name.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GamerController {

    private final GameSessionService service;
    private final SessionRegistry registry;

    @Autowired
    public GamerController(GameSessionService service, SessionRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @RequestMapping("/game/start")
    public String startGame(Model model) {
        System.out.println(registry.getAllPrincipals());
        Player player = (Player) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String commandName = player.getCommandName();
        String gameType = player.getGameSession().getGameType().name().toLowerCase();

        model.addAttribute("commandName", commandName);
        model.addAttribute("message", "Игра скоро начнется");
        model.addAttribute("svgName", "time.svg");

        return String.format("game/player/%s/start", gameType);
    }
}
