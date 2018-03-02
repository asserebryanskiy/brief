package com.name.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class GamerControllerTest {

    @InjectMocks
    private GamerController gamerController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gamerController)
                .build();
    }

    @Test
    public void anonymousRequestsAreRedirectedToIndexPage() throws Exception {
        mockMvc.perform(get("/game"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void userWithRoleCommandCouldAccessGamePage() throws Exception {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMMAND"));
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken("user", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                user);

        mockMvc.perform(get("/game"))
                .andExpect(view().name("brief/gamer/start"));
    }
}
