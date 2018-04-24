package com.name.brief.validation;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.web.dto.PlayerLoginDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(PlayerLoginDtoValidator.class)
public class PlayerLoginDtoValidatorTest {

    private static final String VALID_STR_ID = "id";
    private static final String WRONG_STR_ID = "not-id";

    @Autowired
    private PlayerLoginDtoValidator validator;
    @MockBean
    private GameSessionService gameSessionService;
    @MockBean
    private PlayerAuthenticationService playerAuthenticationService;

    private GameSession gameSession;
    private PlayerLoginDto dto;
    private Errors errors;

    @Before
    public void setUp() throws Exception {
        gameSession = new GameSession();
        gameSession.setStrId(VALID_STR_ID);
        dto = new PlayerLoginDto();
        errors = new BeanPropertyBindingResult(dto, "dto");
        when(gameSessionService.getSession(VALID_STR_ID, LocalDate.now())).thenReturn(gameSession);
        when(gameSessionService.getSession(WRONG_STR_ID, LocalDate.now())).thenReturn(null);
    }

    @Test
    public void ifAuthenticationTypeIsCreateNew_checksOnlyStrId() {
        gameSession.setAuthenticationType(AuthenticationType.CREATE_NEW);
        dto.setName("An");
        dto.setSurname("An");
        dto.setCommandName("an");
        dto.setGameSessionStrId(VALID_STR_ID);

        validator.validate(dto, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ifAuthenticationTypeIsCreateNewAndStrIdIsValid_returnsNoErrors() {
        gameSession.setAuthenticationType(AuthenticationType.CREATE_NEW);
        dto.setGameSessionStrId(VALID_STR_ID);

        validator.validate(dto, errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ifAuthenticationTypeIsCreateNewAndStrIdIsNotValid_returnsWrongGameSessionStrIdError() {
        gameSession.setAuthenticationType(AuthenticationType.CREATE_NEW);
        dto.setGameSessionStrId(WRONG_STR_ID);

        validator.validate(dto, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.validation.wrongGameSessionStrId"));
    }

    @Test
    public void ifAuthenticationTypeIsCreateNewAndStrIdIsNull_returnsWrongGameSessionStrIdError() {
        gameSession.setAuthenticationType(AuthenticationType.CREATE_NEW);
        dto.setGameSessionStrId(null);

        validator.validate(dto, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.validation.wrongGameSessionStrId"));
    }

    @Test
    public void ifAuthenticationTypeIsNameSurnameAndTheyAreNotPresent_returnsEmptyFieldError() {
        gameSession.setAuthenticationType(AuthenticationType.NAME_SURNAME);
        dto.setGameSessionStrId(VALID_STR_ID);

        validator.validate(dto, errors);
        assertThat(errors.getErrorCount(), is(2));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.emptyField"));
        assertThat(errors.getAllErrors().get(1).getCode(), is("player.authentication.emptyField"));
    }

    @Test
    public void ifAuthenticationTypeIsNameSurnameAndOneIsNotRussian_returnsOnlyRussianErrorOnOne() {
        gameSession.setAuthenticationType(AuthenticationType.NAME_SURNAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setSurname("Русский");
        dto.setName("English");

        validator.validate(dto, errors);
        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.onlyRussian"));
    }

    @Test
    public void ifAuthenticationTypeIsNameSurnameAndSurnameIsDashed_returnsNoErrors() {
        gameSession.setAuthenticationType(AuthenticationType.NAME_SURNAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setSurname("Русский-Дефисный");
        dto.setName("Русский");

        validator.validate(dto, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ifAuthenticationTypeIsNameSurnameAndBothAreNotRussian_returnsOnlyRussianErrorOnBoth() {
        gameSession.setAuthenticationType(AuthenticationType.NAME_SURNAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setSurname("English");
        dto.setName("English");

        validator.validate(dto, errors);

        assertThat(errors.getErrorCount(), is(2));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.onlyRussian"));
        assertThat(errors.getAllErrors().get(1).getCode(), is("player.authentication.onlyRussian"));
    }

    @Test
    public void ifAuthenticationTypeIsNameSurnameAndBothAreValid_returnsNoErrors() {
        gameSession.setAuthenticationType(AuthenticationType.NAME_SURNAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setSurname("Русский");
        dto.setName("Русский");

        validator.validate(dto, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void ifAuthenticationTypeIsCommandNameAndItIsNotPresent_returnsEmptyFieldError() {
        gameSession.setAuthenticationType(AuthenticationType.COMMAND_NAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setCommandName(null);

        validator.validate(dto, errors);
        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getFieldErrorCount("commandName"), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.emptyField"));
    }

    @Test
    public void ifAuthenticationTypeIsCommandNameAndItIsNotNumeric_returnsOnlyNumbersError() {
        gameSession.setAuthenticationType(AuthenticationType.COMMAND_NAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setCommandName("Name");

        validator.validate(dto, errors);
        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getFieldErrorCount("commandName"), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.onlyNumbers"));
    }

    @Test
    public void ifAuthenticationTypeIsCommandNameAndCommandIsAlreadyLoggedIn_returnsCommandAlreadyLoggedInError() {
        gameSession.setAuthenticationType(AuthenticationType.COMMAND_NAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setCommandName("1");
        when(playerAuthenticationService.isLoggedIn(Player.constructUsername(
                VALID_STR_ID, LocalDate.now(), "1"
        ))).thenReturn(true);

        validator.validate(dto, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getFieldErrorCount("commandName"), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.commandIsLoggedIn"));
    }

    @Test
    public void ifAuthenticationTypeIsCommandNameAndItIsValid_returnsNoError() {
        gameSession.setAuthenticationType(AuthenticationType.COMMAND_NAME);
        dto.setGameSessionStrId(VALID_STR_ID);
        dto.setCommandName("1");

        validator.validate(dto, errors);
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void checkIfOnlyRussianSymbols_disallowEnglishLetters() {
        validator.checkIfOnlyRussianSymbols("English", "surname", errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.authentication.onlyRussian"));
    }

    @Test
    public void checkIfOnlyRussianSymbols_allowsOneDashInValue() {
        validator.checkIfOnlyRussianSymbols("Русский-дефисный", "surname", errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void checkIfOnlyRussianSymbols_allowsNonCapitalLetters() {
        validator.checkIfOnlyRussianSymbols("дефисный", "surname", errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void checkIfOnlyRussianSymbols_allowsCapitalLetters() {
        validator.checkIfOnlyRussianSymbols("Русский", "surname", errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void checkIfOnlyRussianSymbols_trimsWhitespaces() {
        validator.checkIfOnlyRussianSymbols("Русский ", "surname", errors);

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void checkIfOnlyNumbers_trimsWhitespaces() {
        validator.checkIfOnlyNumbers("1 ", "commandName", errors);

        assertThat(errors.hasErrors(), is(false));
    }
}