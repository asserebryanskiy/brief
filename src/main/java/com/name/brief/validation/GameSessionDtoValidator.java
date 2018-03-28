package com.name.brief.validation;

import com.name.brief.service.GameSessionService;
import com.name.brief.web.dto.GameSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GameSessionDtoValidator implements Validator {
    private final GameSessionService service;

    @Autowired
    public GameSessionDtoValidator(GameSessionService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GameSessionDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GameSessionDto dto = (GameSessionDto) target;

        if (!dto.getNewStrId().matches("[a-zA-Z0-9]+")) {
            errors.rejectValue("newStrId", "gameSessionDto.validation.patternViolation");
        } else if (!dto.getNewStrId().equals(dto.getOldStrId()) &&
                service.getSession(dto.getNewStrId().toLowerCase(), dto.getActiveDate()) != null) {
            errors.rejectValue("newStrId", "gameSessionDto.validation.strIdIsOccupied");
        }
    }
}
