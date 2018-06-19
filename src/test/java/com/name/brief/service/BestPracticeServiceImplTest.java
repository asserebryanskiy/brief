package com.name.brief.service;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.conference.BestPractice;
import com.name.brief.repository.BestPracticeRepository;
import com.name.brief.web.dto.BestPracticeDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringRunner.class)
@WebMvcTest(BestPracticeService.class)
public class BestPracticeServiceImplTest {

    @Autowired
    private BestPracticeService service;

    @MockBean
    private ConferenceService conferenceService;
    @MockBean
    private BestPracticeRepository repository;
    @MockBean
    private SimpMessagingTemplate template;

    @Test
    public void addBestPractice_sendsMessageWithPracticeToModerator() {
        BestPractice bestPractice = new BestPractice();
        service.add(0L, bestPractice);

        verify(template, times(1))
                .convertAndSend("/queue/conference/0/bestPractice", BestPracticeDto.createFrom(bestPractice));
    }
}