package com.name.validation;

import com.name.model.GameSession;
import com.name.service.GameSessionService;
import com.name.web.dto.GameSessionDto;
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
        dto.setStrId("id");
        dto.setActiveDateStr(LocalDate.now().format(GameSessionDto.DATE_FORMATTER));
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
    public void ifStrIdIsFreeForThisDayReturnsError() {
        Errors errors= new BeanPropertyBindingResult(dto, "dto");
        when(service.getSession("id", LocalDate.now())).thenReturn(null);

        validator.validate(dto, errors);

        assertThat(errors.hasErrors(), is(false));
        verify(service, times(1)).getSession("id", LocalDate.now());
    }
}