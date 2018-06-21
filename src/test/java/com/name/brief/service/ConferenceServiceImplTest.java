package com.name.brief.service;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.Phase;
import com.name.brief.repository.GameRepository;
import com.name.brief.utils.TimerTaskScheduler;
import com.name.brief.web.dto.ChangePhaseDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@WebMvcTest(ConferenceService.class)
@RunWith(SpringRunner.class)
public class ConferenceServiceImplTest {
    @Autowired
    private ConferenceService service;

    @MockBean
    private GameRepository repository;
    @MockBean
    private SimpMessagingTemplate template;
    @MockBean
    private TimerTaskScheduler scheduler;

    private static final Long CORRECT_ID = 0L;
    private Conference game;

    @Before
    public void setUp() throws Exception {
        game = new Conference();
        when(repository.findOne(CORRECT_ID)).thenReturn(game);
    }

    @Test
    public void changePhase_shouldChangePhaseIndexOfGame() {
        service.changePhase(CORRECT_ID, 1);

        assertThat(game.getPhaseIndex(), is(1));
    }

    @Test
    public void changePhase_shouldSaveUpdatedGameToRepository() {
        service.changePhase(CORRECT_ID, 1);

        verify(repository, times(1)).save(game);
    }

    @Test
    public void changePhase_shouldStartTimerOfPhaseHasTimer() {
        Phase phase = getFirstTimerPhase();
        service.changePhase(CORRECT_ID, phase.getOrderIndex());

        verify(scheduler, times(1))
                .setUpTimer(CORRECT_ID, phase.getTimerDuration());
    }

    @Test
    public void changePhase_shouldStopCurrentTimers() {
        service.changePhase(CORRECT_ID, 1);

        verify(scheduler, times(1)).stopTimer(CORRECT_ID);
    }

    @Test
    public void changePhase_shouldSendChangePhaseMessageToSubscribers() {
        service.changePhase(CORRECT_ID, 1);

        ChangePhaseDto payload = new ChangePhaseDto(Conference.phases.get(1));
        verify(template, times(1))
                .convertAndSend("/topic/conference/" + CORRECT_ID + "/changePhase", payload);
    }

    private Phase getFirstTimerPhase() {
        return game.getPhases().stream()
                .filter(Phase::isHasTimer)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }



    private String getSecondPhaseName() {
        return game.getPhases().get(1).getEnglishName();
    }
}