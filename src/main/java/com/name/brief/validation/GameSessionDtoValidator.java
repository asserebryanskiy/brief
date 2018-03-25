package com.name.brief.validation;

import com.name.brief.service.GameSessionService;
import com.name.brief.web.dto.GameSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GameSessionDtoValidator implements Validator {
    private String strIdField;
    private String activeDateField;

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

        if (service.getSession(dto.getStrId().toLowerCase(), dto.getActiveDate()) != null) {
            errors.rejectValue("strId", "gameSessionDto.validation.strIdIsOccupied");
        }
    }
}
