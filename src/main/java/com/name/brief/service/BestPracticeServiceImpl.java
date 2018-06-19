package com.name.brief.service;

import com.name.brief.model.games.conference.BestPractice;
import com.name.brief.repository.BestPracticeRepository;
import com.name.brief.web.dto.BestPracticeDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BestPracticeServiceImpl implements BestPracticeService {

    private final ConferenceService conferenceService;
    private final BestPracticeRepository bestPracticeRepository;
    private final SimpMessagingTemplate template;

    public BestPracticeServiceImpl(ConferenceService conferenceService,
                                   BestPracticeRepository bestPracticeRepository,
                                   SimpMessagingTemplate template) {
        this.conferenceService = conferenceService;
        this.bestPracticeRepository = bestPracticeRepository;
        this.template = template;
    }

    @Override
    public BestPractice add(Long gameId, BestPractice bestPractice) {
        bestPractice.setConference(conferenceService.getConference(gameId));

        bestPracticeRepository.save(bestPractice);

        template.convertAndSend("/queue/conference/" + gameId + "/bestPractice",
                BestPracticeDto.createFrom(bestPractice));
        return bestPractice;
    }

    @Override
    public BestPractice changeText(Long bestPracticeId, String newText, Long conferenceId) {
        BestPractice found = bestPracticeRepository.findOne(bestPracticeId);
        if (found == null) {
            throw new IllegalArgumentException("Best practice with id " + bestPracticeId + " was not found");
        }
        found.setText(newText);

        template.convertAndSend("/queue/conference/" + conferenceId + "/changeBestPractice",
                BestPracticeDto.createFrom(found));

        return bestPracticeRepository.save(found);
    }

    @Override
    public List<BestPractice> getBestPracticesFor(Long participantId) {
        return bestPracticeRepository.findByParticipantId(participantId);
    }

    @Override
    public List<BestPractice> getAllBestPractices(Long gameId) {
        return bestPracticeRepository.findByConference(conferenceService.getConference(gameId));
    }

    /**
     * Uses conferenceService because without removing bestPractice from containing it
     * Conference object it still persists in the database.
     *
     * @param conferenceId - id of a Conference obj that contains bestPractice
     * @param bestPracticeId - id of bestPractice
     */
    @Override
    public void delete(Long conferenceId, Long bestPracticeId) {
        conferenceService.removeBestPractice(conferenceId, bestPracticeId);

        template.convertAndSend("/queue/conference/" + conferenceId + "/deleteBestPractice",
                bestPracticeId);
    }
}
