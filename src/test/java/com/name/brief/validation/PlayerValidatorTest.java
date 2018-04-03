package com.name.brief.validation;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(PlayerValidator.class)
public class PlayerValidatorTest {

    @Autowired
    private PlayerValidator validator;

    @MockBean
    private GameSessionService service;

    @MockBean
    private PlayerAuthenticationService playerAuthenticationService;

    private GameSession session;
    private Player player;


    @Before
    public void setUp() throws Exception {
        session = new GameSession.GameSessionBuilder("id")
                .withActiveDate(LocalDate.now())
                .build();
        player = new Player(session, "1");
    }

    @Test
    public void construction_withInvalidGameSessionResultsInConstraintsViolation() {
        given(service.getSession("id", LocalDate.now())).willReturn(null);
        Errors errors = new BeanPropertyBindingResult(player, "player");

        validator.validate(player, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.validation.wrongGameSessionStrId"));
    }

    @Test
    public void construction_withInvalidCommandNameResultsInConstraintsViolation() {
        given(service.getSession(session.getStrId(), session.getActiveDate())).willReturn(session);
        player.setCommandName("invalid");
        Errors errors = new BeanPropertyBindingResult(player, "player");

        validator.validate(player, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.validation.noCommandWithSuchName"));
    }

    @Test
    public void construction_withLoggedInCommandNameResultsInConstraintsViolation() {
        player.setLoggedIn(true);
        List<Player> players = Collections.singletonList(player);
        session.setPlayers(players);
        given(service.getSession(session.getStrId(), session.getActiveDate())).willReturn(session);
        given(playerAuthenticationService.isLoggedIn(player)).willReturn(true);
        Errors errors = new BeanPropertyBindingResult(player, "player");

        validator.validate(player, errors);

        assertThat(errors.getErrorCount(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("player.validation.commandIsLoggedIn"));
    }

    @Test
    public void construction_withValidGameSessionResultsInNoErrors() {
        List<Player> players = Collections.singletonList(player);
        session.setPlayers(players);
        given(service.getSession(session.getStrId(), session.getActiveDate())).willReturn(session);
        given(playerAuthenticationService.isLoggedIn(player)).willReturn(false);
        Errors errors = new BeanPropertyBindingResult(player, "player");

        validator.validate(player, errors);

        assertThat(errors.hasErrors(), is(false));
    }
}