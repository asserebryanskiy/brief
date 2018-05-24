package com.name.brief.web.controller;

import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.roleplay.*;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.name.brief.utils.RolePlayUtils.findPlayerData;

@Controller
public class GamerController {

    private final GameSessionService service;
    private final PlayerService playerService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public GamerController(GameSessionService service,
                           PlayerService playerService,
                           PlayerAuthenticationService playerAuthenticationService) {
        this.service = service;
        this.playerService = playerService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @RequestMapping("/game")
    // ToDo: remove this chaos! No more logic in my controllers!
    public String startGame(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName());

        // if player is null it means it was deleted from gameSession object
        if (player == null) {
            // if user managed to get to this mapping, it means he can only
            // be authenticated as Player, thus casting is justified
            Player authenticated = (Player) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            playerAuthenticationService.logout(authenticated);
            return "redirect:/";
        }

        model.addAttribute("playerId", player.getId());

        GameSession gameSession = service.getSession(player.getGameSession().getId());
        model.addAttribute("gameSession", gameSession);
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
