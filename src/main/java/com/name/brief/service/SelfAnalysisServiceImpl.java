package com.name.brief.service;

import com.name.brief.model.games.conference.SelfAnalysis;
import com.name.brief.repository.SelfAnalysisRepository;
import com.name.brief.web.dto.SelfAnalysisDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SelfAnalysisServiceImpl implements SelfAnalysisService {

    private final SelfAnalysisRepository repository;
    private final ConferenceService conferenceService;
    private final SimpMessagingTemplate template;

    public SelfAnalysisServiceImpl(SelfAnalysisRepository repository,
                                   ConferenceService conferenceService,
                                   SimpMessagingTemplate template) {
        this.repository = repository;
        this.conferenceService = conferenceService;
        this.template = template;
    }

    @Override
    public SelfAnalysis add(Long conferenceId, SelfAnalysis selfAnalysis) {
        if (selfAnalysis.getId() != null) return update(conferenceId, selfAnalysis);

        selfAnalysis.setConference(conferenceService.getConference(conferenceId));

        repository.save(selfAnalysis);

        if (selfAnalysis.isReadyToShare()) {
            template.convertAndSend("/topic/conference/" + conferenceId + "/newSelfAnalysis",
                    SelfAnalysisDto.createFrom(selfAnalysis));
        }
        return selfAnalysis;
    }

    @Override
    public SelfAnalysis update(Long conferenceId, SelfAnalysis selfAnalysis) {
        if (selfAnalysis.getId() == null) return add(conferenceId, selfAnalysis);

        return repository.save(selfAnalysis);
    }

    @Override
    public SelfAnalysis getFor(Long participantId) {
        return repository.findByParticipantId(participantId);
    }
}
