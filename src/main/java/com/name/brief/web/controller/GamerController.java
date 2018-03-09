package com.name.brief.web.controller;

import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.utils.BriefUtils;
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
    public GamerController(GameSessionService service,
                           SessionRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @RequestMapping("/game")
    public String startGame(Model model) {
        Player principal = (Player) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GameSession gameSession = service.getSession(principal.getGameSession().getId());
        Player player = gameSession.getPlayers().stream()
                .filter(p -> p.getId().equals(principal.getId()))
                .findAny()
                .get();
        String commandName = player.getCommandName();
        String gameType = gameSession.getGame().getEnglishName();
        int currentRoundIndex = gameSession.getCurrentRoundIndex();
        int currentPhase = gameSession.getCurrentPhaseNumber();
        Decision decision = player.getDecision(currentRoundIndex);

        model.addAttribute("commandName", commandName);
        model.addAttribute("round", currentRoundIndex);
        model.addAttribute("gameSessionId", gameSession.getId());
        model.addAttribute("currentPhaseNumber", currentPhase);
        model.addAttribute("correctAnswer", gameSession.getGame()
                .getCorrectAnswer(currentRoundIndex));
        model.addAttribute("statsList", gameSession.getStatsList());

        // if player already sent decision add it to the answer matrix and block decisions sending
        if (decision.getAnswer() != null) {
            model.addAttribute("answerTable", BriefUtils.getAnswerMatrix(decision));
            model.addAttribute("answersSubmitted", true);
        } else {
            model.addAttribute("answerTable", new boolean[5][5]);
        }



        return "game/player/" + gameType;
    }
}
