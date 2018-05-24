package com.name.brief.service;

import com.name.brief.exception.GameSessionAlreadyExistsException;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.User;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.repository.PlayerRepository;
import com.name.brief.web.dto.GameSessionDto;
import com.name.brief.web.dto.NextPhaseMessage;
import com.name.brief.web.dto.PlayerLoginDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(GameSessionService.class)
public class GameSessionServiceImplTest {
    @Autowired
    private GameSessionService service;

    @MockBean
    private GameSessionRepository repository;

    @MockBean
    private PlayerRepository playerRepository;

    @Test(expected = GameSessionAlreadyExistsException.class)
    public void save_alreadyExistingSessionResultsInException() {
        GameSession session = new GameSession();
        when(repository.findByStrIdAndActiveDate(any(), any())).thenReturn(session);
        service.save(session);
    }

    @Test
    public void incrementPhase_returnsZeroIfCurrentPhaseIsLast() {
        NextPhaseMessage message = new NextPhaseMessage(0, 30);

        service.changePhase(0L, message.getPhaseNumber());

        verify(repository, times(1))
                .changePhase(0L, message.getPhaseNumber());
    }

    @Test
    public void update_updatesDateInPlayersUsernames() {
        GameSession session = new GameSession.GameSessionBuilder("id").build();
        GameSessionDto dto = GameSessionDto.createFrom(session);
        dto.setActiveDate(LocalDate.now().minusDays(1));
        when(repository.findOne(dto.getGameSessionId())).thenReturn(session);

        service.update(dto);

        session.getPlayers().forEach(p -> {
            String expected = Player.constructUsername(session.getStrId(), dto.getActiveDate(), p.getCommandName());
            assertThat(p.getUsername(), is(expected));
        });
    }

    @Test
    public void update_updatesStrIdInPlayersUsernames() {
        GameSession session = new GameSession.GameSessionBuilder("id").build();
        GameSessionDto dto = GameSessionDto.createFrom(session);
        dto.setNewStrId("newId");
        when(repository.findOne(dto.getGameSessionId())).thenReturn(session);

        service.update(dto);

        session.getPlayers().forEach(p -> {
            String expected = Player.constructUsername(dto.getNewStrId(), session.getActiveDate(), p.getCommandName());
            assertThat(p.getUsername(), is(expected));
        });
    }

    @Test
    public void addPlayer_givesPlayerUsername() {
        GameSession session = new GameSession.GameSessionBuilder("id").build();
        Player player = new Player();
        when(repository.findOne(session.getId())).thenReturn(session);
        when(playerRepository.save(player)).thenAnswer((s) -> {
            player.setId(1L);
            return null;
        });

        service.addPlayer(player, session);

        assertThat(player.getUsername(), is("player1"));
    }
}