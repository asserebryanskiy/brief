package com.name.brief.service;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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

    @Test
    public void getCurrentlyLoggedPlayers_returnsOnlyPlayersOfProvidedSession() {
        GameSession session = new GameSession.GameSessionBuilder("id").build();
        session.setId(1L);
        GameSession other = new GameSession.GameSessionBuilder("id2").build();
        other.setId(2L);
        List<Object> allPlayersList =
                new ArrayList<>(session.getPlayers().size() + other.getPlayers().size());
        allPlayersList.addAll(session.getPlayers());
        allPlayersList.addAll(other.getPlayers());
        service.setSessionRegistry(registry);
        when(registry.getAllPrincipals()).thenReturn(allPlayersList);

        Set<String> result = service.getAuthenticatedPlayersUsernames(1L);

        assertThat(result, hasSize(session.getPlayers().size()));
        session.getPlayers().stream()
                .map(Player::getUsername)
                .forEach(username -> assertThat(result, hasItem(username)));
    }
}