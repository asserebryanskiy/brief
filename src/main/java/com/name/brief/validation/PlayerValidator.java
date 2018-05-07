package com.name.brief.validation;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PlayerValidator implements Validator {
    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public PlayerValidator(GameSessionService gameSessionService,
                           PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        GameSession provided = player.getGameSession();

        GameSession gameSession = gameSessionService
                .getSession(provided.getStrId().trim(), provided.getActiveDate());
        if (gameSession == null) {
            errors.rejectValue("gameSession", "player.validation.wrongGameSessionStrId");
        } else {
            if (gameSession.getPlayers() == null || gameSession.getPlayers().stream()
                    .map(Player::getCommandName)
                    .noneMatch(name -> name.equals(player.getCommandName().trim()))) {
                errors.rejectValue("commandName", "player.validation.noCommandWithSuchName");
            } else {
                Player found = gameSession.getPlayers().stream()
                        .filter(c -> c.getCommandName().equals(player.getCommandName().trim()))
                        .findAny()
                        .orElse(null);  // ignore because we've already checked that player exists
                //noinspection ConstantConditions
                if (playerAuthenticationService.isLoggedIn(found)) {
                    errors.rejectValue("username", "player.validation.commandIsLoggedIn");
                }
            }
        }
    }
}
