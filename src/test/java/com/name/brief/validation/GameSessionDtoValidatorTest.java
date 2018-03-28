package com.name.brief.validation;

import com.name.brief.model.GameSession;
import com.name.brief.service.GameSessionService;
import com.name.brief.web.dto.GameSessionDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(GameSessionDtoValidator.class)
public class GameSessionDtoValidatorTest {

    @Autowired
    private GameSessionDtoValidator validator;

    @MockBean
    private GameSessionService service;

    private GameSessionDto dto;

    @Before
    public void setUp() throws Exception {
        dto = new GameSessionDto();
        dto.setNewStrId("id");
        dto.setActiveDate(LocalDate.now());
    }

    @Test
    public void ifStrIdIsOccupiedForThisDayReturnsError() {
        Errors errors= new BeanPropertyBindingResult(dto, "dto");
        when(service.getSession("id", LocalDate.now())).thenReturn(new GameSession());

        validator.validate(dto, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("gameSessionDto.validation.strIdIsOccupied"));
        verify(service, times(1)).getSession("id", LocalDate.now());
    }

    @Test
    public void ifStrIdIsFreeForThisDayReturnsNoErrors() {
        Errors errors= new BeanPropertyBindingResult(dto, "dto");
        when(service.getSession("id", LocalDate.now())).thenReturn(null);

        validator.validate(dto, errors);

        assertThat(errors.hasErrors(), is(false));
        verify(service, times(1)).getSession("id", LocalDate.now());
    }

    @Test
    public void ifNewStrIdIsEqualToOldStrIdReturnsNoErrors() {
        Errors errors = new BeanPropertyBindingResult(dto, "dto");
        dto.setOldStrId(dto.getNewStrId());

        validator.validate(dto, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ifNewStrIdDoesNotMatchesPatternReturnsPatternViolationError() {
        Errors errors = new BeanPropertyBindingResult(dto, "dto");
        dto.setNewStrId("ABC90авгода");

        validator.validate(dto, errors);

        assertThat(errors.getAllErrors(), hasSize(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("gameSessionDto.validation.patternViolation"));
    }
}