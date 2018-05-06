package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.*;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.RiskMap;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.UserRepository;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.RolePlayService;
import com.name.brief.utils.TimerTaskScheduler;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile({"dev", "localPostgre"})
public class DevDatabaseLoader implements ApplicationRunner {

    private final GameSessionService gameSessionService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RolePlayService rolePlayService;
    private final TimerTaskScheduler scheduler;

    @Autowired
    public DevDatabaseLoader(GameSessionService gameSessionService,
                             UserRepository userRepository,
                             GameRepository gameRepository,
                             RolePlayService rolePlayService,
                             TimerTaskScheduler scheduler) {
        this.gameSessionService = gameSessionService;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.rolePlayService = rolePlayService;
        this.scheduler = scheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<User> users = new ArrayList<>(2);
        String password = SecurityConfig.passwordEncoder.encode("password");
        User moderator1 = new User("moderator1", password, Role.MODERATOR.getRole());
        User moderator2 = new User("moderator2", password, Role.MODERATOR.getRole());
        users.add(moderator1);
        users.add(moderator2);
        users.add(new User("admin", password, Role.ADMIN.getRole()));
        userRepository.save(users);

        GameSession session = new GameSession.GameSessionBuilder("brief")
                .withAuthenticationType(AuthenticationType.COMMAND_NAME)
                .withUser(moderator1)
                .build();

        session.getPlayers().forEach(p -> {
            String[] correctAnswers = ((Brief) session.getGame()).getCorrectAnswers();
            String[] additions = {"", "A2", "A2A4", "A2A4B4", "A2A4B4C4"};
            for (int i = 0; i < p.getDecisions().size(); i++) {
                p.getDecisions().get(i).setAnswer(
                        correctAnswers[i] + additions[new Random().nextInt(additions.length)]);
            }
        });

        GameSession session2 = new GameSession.GameSessionBuilder("testtest")
                .withUser(moderator1)
                .build();
        GameSession session3 = new GameSession.GameSessionBuilder("otherUserSession")
                .withUser(moderator2)
                .build();
        GameSession pastSession = new GameSession.GameSessionBuilder("otherUserSession")
                .withActiveDate(LocalDate.now().minusDays(1))
                .withUser(moderator1)
                .build();
        GameSession riskMap = new GameSession.GameSessionBuilder("risk")
                .withGame(new RiskMap())
                .withAuthenticationType(AuthenticationType.NAME)
                .withUser(moderator1)
                .build();
        RolePlay game = new RolePlay();
        GameSession rolePlay = new GameSession.GameSessionBuilder("role")
                .withGame(game)
                .withUser(moderator1)
                .build();
        game.setPhaseIndex(4);
        gameSessionService.save(session);
        gameSessionService.save(session2);
        gameSessionService.save(session3);
        gameSessionService.save(pastSession);
        gameSessionService.save(riskMap);
        gameSessionService.save(rolePlay);

//        PlayerLoginDto dto = new PlayerLoginDto();
//        dto.setGameSessionStrId(session.getStrId());
//        gameSessionService.addPlayer(dto, rolePlay);
//        gameSessionService.addPlayer(dto, rolePlay);
//
//        rolePlayService.changePhase(2, rolePlay.getGame().getId());
//        rolePlayService.changePhase(3, rolePlay.getGame().getId());
//        DoctorAnswerDto doctorAnswerDto = new DoctorAnswerDto();
//        doctorAnswerDto.getExpertiseEstimations().put(SalesmanCompetency.KNOWLEDGE.getCssClassName(), "high");
//        rolePlayService.saveDoctorAnswers(
//                rolePlay.getGame().getId(),
//                doctorAnswerDto,
//                gameSessionService.getSession(rolePlay.getId()).getPlayers().get(0).getId()
//        );

    }
}
