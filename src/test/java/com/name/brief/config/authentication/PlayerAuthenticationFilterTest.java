package com.name.brief.config.authentication;

import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PlayerAuthenticationFilter.class)
public class PlayerAuthenticationFilterTest {
    private PlayerAuthenticationFilter filter;
    @MockBean
    private GameSessionService gameSessionService;
    @MockBean
    private PlayerAuthenticationService playerAuthenticationService;

    @Before
    public void setUp() throws Exception {
        filter = new PlayerAuthenticationFilter(gameSessionService, playerAuthenticationService, messageSource);
    }

    @Test
    public void obtainUsername_trimsWhitespaces() {
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setParameter("gameSession.strId", "id ");
        ((MockHttpServletRequest) request).setParameter("commandName", "1 ");

        assertThat(filter.obtainUsername(request), is("id" + LocalDate.now().toString() + "1"));
    }
}