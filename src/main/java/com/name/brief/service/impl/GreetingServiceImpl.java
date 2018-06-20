package com.name.brief.service.impl;

import com.name.brief.model.games.conference.GreetingAnswer;
import com.name.brief.repository.GreetingAnswerRepository;
import com.name.brief.service.ConferenceService;
import com.name.brief.service.GreetingService;
import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {
    private final ConferenceService conferenceService;
    private final GreetingAnswerRepository repository;

    public GreetingServiceImpl(ConferenceService conferenceService,
                               GreetingAnswerRepository repository) {
        this.conferenceService = conferenceService;
        this.repository = repository;
    }

    @Override
    public Long addAnswer(Long conferenceId, GreetingAnswer greetingAnswer) {
        greetingAnswer.setConference(conferenceService.getConference(conferenceId));
        return repository.save(greetingAnswer).getId();
    }

    @Override
    public void updateAnswer(Long greetingAnswerId, int imgIndex, String comment) {
        GreetingAnswer answer = repository.findOne(greetingAnswerId);
        answer.setImgIndex(imgIndex);
        answer.setComment(comment);
        repository.save(answer);
    }

    @Override
    public GreetingAnswer getGreetingAnswer(Long conferenceId, Long participantId) {
        return conferenceService.getConference(conferenceId).getGreetingAnswers().stream()
                .filter(ans -> ans.getParticipantId().equals(participantId))
                .findAny()
                .orElse(null);
    }
}
