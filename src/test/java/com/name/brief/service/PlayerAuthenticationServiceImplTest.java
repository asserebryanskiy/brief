package com.name.brief.service;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(PlayerAuthenticationService.class)
public class PlayerAuthenticationServiceImplTest {
    @Autowired
    private PlayerAuthenticationService service;
    @MockBean
    private SessionRegistry registry;
    @MockBean
    private SimpMessagingTemplate template;
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private GameSessionServiceImpl gameSessionService;

    @Before
    public void setUp() throws Exception {
        service.setSessionRegistry(registry);
    }

    @Test
    public void getCurrentlyLoggedPlayers_returnsOnlyPlayersOfProvidedSession() {
        // initialize gameSessions
        GameSession session = new GameSession.GameSessionBuilder("id").build();
        session.setId(1L);
        GameSession other = new GameSession.GameSessionBuilder("id2").build();
        other.setId(2L);
        for (int i = 0; i < 5; i++) {
            Player player = new Player(session);
            player.setUsername("Session" + String.valueOf(i));
            session.getPlayers().add(player);
            Player otherPlayer = new Player(other);
            otherPlayer.setUsername("Other" + String.valueOf(i));
            other.getPlayers().add(otherPlayer);
        }

        // create players list to be tracked by registry
        List<Object> allPlayersList =
                new ArrayList<>(session.getPlayers().size() + other.getPlayers().size());
        allPlayersList.addAll(session.getPlayers());
        allPlayersList.addAll(other.getPlayers());

        // mock registry
        when(registry.getAllPrincipals()).thenReturn(allPlayersList);
        when(registry.getAllSessions(session.getPlayers().get(0), false)).thenReturn(Collections.singletonList(null));
        when(registry.getAllSessions(session.getPlayers().get(1), false)).thenReturn(Collections.singletonList(null));

        Set<String> result = service.getAuthenticatedPlayersUsernames(1L);

        assertThat(result, hasSize(2));
        assertThat(result, hasItem(session.getPlayers().get(0).getUsername()));
        assertThat(result, hasItem(session.getPlayers().get(1).getUsername()));
    }

    @Test
    public void isLoggedIn_returnsTrueIfPlayerWithProvidedUsernameIsLoggedIn() {
        Player player = new Player();
        String username = "username";
        player.setUsername(username);
        List<Object> allPrincipals = Collections.singletonList(player);
        when(registry.getAllPrincipals()).thenReturn(allPrincipals);
        when(registry.getAllSessions(player, false)).thenReturn(Collections.singletonList(null));

        assertThat(service.isLoggedIn(username), is(true));
    }

    @Test
    public void isLoggedIn_returnsFalseIfPlayerWithProvidedUsernameIsLoggedIn() {
        List<Object> allPrincipals = Collections.emptyList();
        when(registry.getAllPrincipals()).thenReturn(allPrincipals);

        assertThat(service.isLoggedIn("any"), is(false));
    }
}