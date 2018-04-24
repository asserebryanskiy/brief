package com.name.brief.validation;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class PlayerLoginDtoValidator implements Validator {

    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public PlayerLoginDtoValidator(GameSessionService gameSessionService,
                                   PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PlayerLoginDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PlayerLoginDto dto = (PlayerLoginDto) target;

        GameSession session = gameSessionService.getSession(dto.getGameSessionStrId(), LocalDate.now());
        if (session == null) {
            errors.rejectValue("gameSessionStrId", "player.validation.wrongGameSessionStrId");
        } else {
            // check spelling
            for (String[] inputArr : session.getAuthenticationType().getInputs()) {
                String key = inputArr[0];
                switch (key) {
                    case "name":
                        checkIfOnlyRussianSymbols(dto.getName(), key, errors);
                        break;
                    case "surname":
                        checkIfOnlyRussianSymbols(dto.getSurname(), key, errors);
                        break;
                    case "commandName":
                        checkIfOnlyNumbers(dto.getCommandName(), key, errors);
                        break;
                }
            }

            // check if already logged in
            if (session.getAuthenticationType() == AuthenticationType.COMMAND_NAME) {
                String username = Player.constructUsername(
                        session.getStrId(), LocalDate.now(), dto.getCommandName());
                if (playerAuthenticationService.isLoggedIn(username)) {
                    errors.rejectValue("commandName", "player.authentication.commandIsLoggedIn");
                }
            }
        }
    }

    public void checkIfOnlyNumbers(String value, String key, Errors errors) {
        if (value == null || value.length() == 0)
            errors.rejectValue(key, "player.authentication.emptyField");
        else {
            try {
                Integer.parseInt(value.trim());
            } catch (NumberFormatException nfe) {
                errors.rejectValue(key, "player.authentication.onlyNumbers");
            }
        }
    }

    public void checkIfOnlyRussianSymbols(String value, String key, Errors errors) {
        if (value == null || value.length() == 0)
            errors.rejectValue(key, "player.authentication.emptyField");
        else {
            if (!value.trim().matches("[а-яА-Я]+(-[а-яА-Я]+)?"))
                errors.rejectValue(key, "player.authentication.onlyRussian");
        }
    }
}
