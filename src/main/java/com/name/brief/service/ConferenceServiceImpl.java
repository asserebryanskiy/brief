package com.name.brief.service;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import com.name.brief.model.games.conference.SelfAnalysis;
import com.name.brief.repository.GameRepository;
import com.name.brief.utils.GameUtils;
import com.name.brief.utils.TimerTaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConferenceServiceImpl implements ConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceServiceImpl.class);

    private final GameRepository repository;
    private final TimerTaskScheduler scheduler;
    private final SimpMessagingTemplate template;

    public ConferenceServiceImpl(GameRepository repository,
                                 TimerTaskScheduler scheduler,
                                 SimpMessagingTemplate template) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.template = template;
    }

    @Override
    public void changePhase(Long gameId, int phaseIndex) {
        // find game
        Conference game = getConference(gameId);

        // stop timer
        scheduler.stopTimer(gameId);

        // change game's current phase index
        game.setPhaseIndex(phaseIndex);

        // start new timer if needed
        Phase phase = game.getPhases().get(phaseIndex);
        if (phase.isHasTimer()) {
            scheduler.setUpTimer(gameId, phase.getTimerDuration());
        }

        // notify subscribers
        template.convertAndSend("/topic/conference/" + gameId + "/changePhase",
                phase.getEnglishName());

        // update persistent item
        repository.save(game);
    }

    @Override
    public Conference getConference(Long conferenceId) {
        Game found = repository.findOne(conferenceId);
        if (found == null) {
            logger.error("game for id {} was not found", conferenceId);
            throw new IllegalArgumentException();
        }

        if (!(found instanceof Conference)) {
            logger.error("game for id {} was not of type Conference", conferenceId);
            throw new IllegalArgumentException();
        }

        return (Conference) found;
    }

    @Override
    public void removeBestPractice(Long conferenceId, Long bestPracticeId) {
        Conference conference = getConference(conferenceId);
        conference.getBestPractices().removeIf(bp -> bp.getId().equals(bestPracticeId));
        repository.save(conference);
    }

    @Override
    public SelfAnalysis addSelfAnalysis(Long conferenceId, SelfAnalysis selfAnalysis) {
        Conference conference = getConference(conferenceId);
        conference.getSelfAnalyses().add(selfAnalysis);
        repository.save(conference);

        if (selfAnalysis.isReadyToShare()) {
            template.convertAndSend("/topic/conference/" + conferenceId + "/newSelfAnalysis",
                    selfAnalysis);
        }

        return selfAnalysis;
    }
}
