package com.name.brief.service;

import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.repository.GameRepository;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(RolePlayService.class)
public class RolePlayServiceImplTest {

    @Autowired
    private RolePlayService service;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private SimpMessagingTemplate template;

    @Test
    public void setUp_changesRolePLayStrategyToProvidedInDto() throws Exception {
        RolePlay game = new RolePlay();
        when(gameRepository.findOne(game.getId())).thenReturn(game);
        RolePlaySettingsDto dto = new RolePlaySettingsDto();
        dto.setStrategy(1);

        service.setUp(game.getId(), dto);

        assertThat(game.getStrategyNumber(), is(dto.getStrategy()));
        verify(gameRepository).save(game);
    }

    @Test(expected = WrongGameTypeException.class)
    public void ifProvidedToSetUpGameIdIsNotOfRolePlayGameThrowsException() throws Exception {
        when(gameRepository.findOne(0L)).thenReturn(new Brief());

        service.setUp(0L, null);
    }
}