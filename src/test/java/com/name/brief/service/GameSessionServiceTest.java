package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.model.GameSession;
import com.name.brief.model.User;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.web.dto.NextPhaseMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

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

        assertThat(service.isSessionActive(new GameSession.GameSessionBuilder("id").build()), is(false));
    }

    @Test(expected = GameSessionAlreadyExistsException.class)
    public void save_alreadyExistingSessionResultsInException() {
        GameSession session = new GameSession();
        when(repository.findByStrIdAndActiveDate(any(), any())).thenReturn(session);
        service.save(session);
    }

    @Test
    public void save_savesAlsoNewUserForEveryCommand() {
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withNumberOfCommands(3)
                .withUser(new User())
                .build();
        when(repository.findByStrIdAndActiveDate(any(), any())).thenReturn(null);

        service.save(session);

        verify(playerService, times(3)).save(any());
    }

    @Test
    public void incrementPhase_returnsZeroIfCurrentPhaseIsLast() {
        NextPhaseMessage message = new NextPhaseMessage(0, 30);

        service.changePhase(0L, message.getPhaseNumber());

        verify(repository, times(1))
                .changePhase(0L, message.getPhaseNumber());
    }

    @Test
    public void getCorrectAnswerForCurrentRound_returnsProperValue() {
        GameSession session = new GameSession();
        session.setCurrentRoundIndex(2);
        Game game = new Brief();
        session.setGame(game);
        when(repository.findOne(0L)).thenReturn(session);

        String found = service.getCorrectAnswerForCurrentRound(0L);

        assertThat(found, is(game.getCorrectAnswer(2)));
    }
}