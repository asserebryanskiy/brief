package com.name.brief.service;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.RiskMap;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.GameSessionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(RiskMapService.class)
public class RiskMapServiceImplTest {
    @Autowired
    private RiskMapService service;

    @MockBean
    private GameSessionService gameSessionService;
    @MockBean
    private GameRepository gameRepository;

    @Test
    public void changeSector_worksProperly() {
        RiskMap riskMap = new RiskMap();
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withGame(riskMap)
                .build();
        when(gameSessionService.getSession(0L)).thenReturn(session);

        service.changeSectorNumber("1", 0L);

        assertThat(riskMap.getCurrentSectorNumber(), is(1));
        verify(gameRepository, times(1)).save(riskMap);
    }
}
