package com.name.brief.web.controller;

import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.roleplay.*;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.utils.RolePlayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.name.brief.utils.RolePlayUtils.findPlayerData;

@Controller
public class GamerController {

    private final GameSessionService service;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public GamerController(GameSessionService service,
                           PlayerAuthenticationService playerAuthenticationService) {
        this.service = service;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @RequestMapping("/game")
    public String startGame(Model model) {
        Player principal = (Player) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GameSession gameSession = service.getSession(principal.getGameSession().getId());
        Player player = gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(principal.getId()))
                .findAny()
                .orElse(null);
        // if player is null it means it was deleted from gameSession object
        if (player == null) {
            playerAuthenticationService.logout(principal);
            return "redirect:/";
        }


        Decision decision = player.getDecision(gameSession.getCurrentRoundIndex());
        model.addAttribute("commandName", player.getCommandName());
        model.addAttribute("gameSession", gameSession);
        model.addAttribute("decision", decision);
        model.addAttribute("playerId", player.getId());
        if (!gameSession.timerIsRunning()) {
            model.addAttribute("disableAnswerSend", true);
        }

        if (gameSession.getGame() instanceof RolePlay) {
            PlayerData data = findPlayerData(player.getId(), ((RolePlay) gameSession.getGame()).getPlayersData());
            if (data == null) {
                data = new PlayerData();
                data.setRole(SalesmanRole.SALESMAN_1);
                data.setLocation(new PlayerLocation(0,0));
            }
            model.addAttribute("playerData", data);
        }

        return "game/" + gameSession.getGame().getEnglishName();
    }
}
