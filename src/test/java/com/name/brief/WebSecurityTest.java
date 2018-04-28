package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.User;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.GameSessionServiceImpl;
import com.name.brief.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
@TestPropertySource("classpath:application.properties")
public class WebSecurityTest {

    @Autowired
    private WebApplicationContext context;

    @Mock
    private GameSessionServiceImpl gameSessionService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void adminPageIsNotAllowedToAnonymousUser() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(redirectedUrl("http://localhost/login/admin"));
    }

    @Test
    @WithAnonymousUser
    public void gamePageIsNotAllowedToAnonymousUser() throws Exception {
        mockMvc.perform(get("/game"))
                .andExpect(redirectedUrl("http://localhost/"));
    }

    /*@Test
    public void usersWithPlayerRoleAreAllowedToGamePage() throws Exception {
        GameSession gameSession = new GameSession.GameSessionBuilder("id").build();
        gameSession.setId(1L);
        Player player = gameSession.getPlayers().get(0);
        when(gameSessionService.getSession(any(Long.class))).thenReturn(gameSession);
        mockMvc.perform(get("/game").with(user(player)))
                .andExpect(view().name("game/player/brief"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("commandName", player.getCommandName()))
                .andExpect(model().attribute("round", 0))
                .andExpect(model().attribute("gameSessionId", gameSession.getId()))
                .andExpect(model().attribute("currentPhaseNumber", 0))
                .andExpect(model().attribute("correctAnswer", gameSession.getGame()
                        .getCorrectAnswer(0)))
                .andExpect(model().attribute("statsList", gameSession.getStatsList()))
                .andExpect(model().attribute("answerTable", new boolean[5][5]));
    }*/

    /*@Test
    public void usersWithModeratorRoleAreAllowedToModeratorPage() throws Exception {
        User moderator = new User();
        moderator.setRole("ROLE_MODERATOR");
        moderator.setId(1L);
        mockMvc.perform(get("/moderator").with(user(moderator)))
                .andExpect(view().name("administration/moderator/index"))
                .andExpect(status().isOk());
    }*/

    @Test
    @WithMockUser(roles = {"MODERATOR"})
    public void usersWithModeratorRoleAreNotAllowedToAdminPage() throws Exception {
        mockMvc.perform(get("/admin"))
//                .andExpect(redirectedUrl("/"))
                .andExpect(status().isForbidden());
    }

}