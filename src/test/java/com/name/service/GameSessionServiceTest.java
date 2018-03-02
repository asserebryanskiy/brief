package com.name.service;

import com.name.exception.GameSessionAlreadyExistsException;
import com.name.model.GameSession;
import com.name.repository.GameSessionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static com.name.model.GameSession.GameSessionBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(GameSessionService.class)
public class GameSessionServiceTest {
    @Autowired
    private GameSessionService service;

    @MockBean
    private GameSessionRepository repository;
    @MockBean
    private PlayerService playerService;

    @Test
    public void isSessionActive_returnsFalseIfNoSessionWithThisStrId() {
        when(repository.findByStrIdAndActiveDate("non-existing-id", LocalDate.now()))
                .thenReturn(null);

        assertThat(service.isSessionActive(new GameSessionBuilder("id").build()), is(false));
    }

    @Test(expected = GameSessionAlreadyExistsException.class)
    public void save_alreadyExistingSessionResultsInException() {
        GameSession session = new GameSession();
        when(repository.findByStrIdAndActiveDate(any(), any())).thenReturn(session);
        service.save(session);
    }

    @Test
    public void save_savesAlsoNewUserForEveryCommand() {
        GameSession session = new GameSessionBuilder("id")
                .withNumberOfCommands(3).build();
        when(repository.findByStrIdAndActiveDate(any(), any())).thenReturn(null);

        service.save(session);

        verify(playerService, times(3)).save(any());
    }
}