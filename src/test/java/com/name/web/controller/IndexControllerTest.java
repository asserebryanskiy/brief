package com.name.web.controller;

import com.name.model.GameSession;
import com.name.model.Player;
import com.name.service.GameSessionService;
import com.name.validation.PlayerValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class IndexControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private IndexController controller;

    @Mock
    private PlayerValidator validator;

    @Mock
    private GameSessionService service;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void indexRequestReturnsIndexView() throws Exception {
        when(validator.supports(Player.class)).thenReturn(true);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}