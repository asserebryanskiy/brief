package com.name.validation;

import com.name.model.Player;
import com.name.model.GameSession;
import com.name.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PlayerValidator implements Validator {
    private final GameSessionService service;

    @Autowired
    public PlayerValidator(GameSessionService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        GameSession provided = player.getGameSession();

        GameSession gameSession = service.getSession(provided.getStrId(), provided.getActiveDate());
        if (gameSession == null) {
            errors.rejectValue("gameSession", "player.validation.wrongGameSessionStrId");
        } else {
            if (gameSession.getPlayers() == null || gameSession.getPlayers().stream()
                    .map(Player::getCommandName)
                    .noneMatch(name -> name.equals(player.getCommandName()))) {
                errors.rejectValue("commandName", "player.validation.noCommandWithSuchName");
            } else {
                Player found = gameSession.getPlayers().stream()
                        .filter(c -> c.getCommandName().equals(player.getCommandName()))
                        .findAny()
                        .orElse(null);  // ignore because we've already checked that player exists
                //noinspection ConstantConditions
                if (found.isLoggedIn()) {
                    errors.rejectValue("loggedIn", "player.validation.commandIsLoggedIn");
                }
            }
        }
    }
}
