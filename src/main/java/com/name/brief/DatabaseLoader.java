package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import com.name.brief.model.Role;
import com.name.brief.model.User;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.RiskMap;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.UserRepository;
import com.name.brief.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DatabaseLoader implements ApplicationRunner {

    private final GameSessionService gameSessionService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Autowired
    public DatabaseLoader(GameSessionService gameSessionService,
                          UserRepository userRepository,
                          GameRepository gameRepository) {
        this.gameSessionService = gameSessionService;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
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
        Game brief = new Brief();
        Game riskMap = new RiskMap();
        gameRepository.save(brief);
        gameRepository.save(riskMap);

        GameSession session = new GameSession.GameSessionBuilder("brief")
                .withNumberOfCommands(5)
                .withGame(brief)
                .withUser(moderator1)
                .build();

        final String[] answers = brief.getCorrectAnswers();
        final String[] additions = {"", "D4", "D4D2", "D4D2D1B4B2B1"};
        session.getPlayers().forEach(p -> {
            if (!p.getCommandName().equals("2")) {
                Decision decision = p.getDecision(0);
                decision.setAnswer(answers[0] + additions[Math.round((float) (Math.random() * 3.0))]);
            }
        });

        GameSession session2 = new GameSession.GameSessionBuilder("testtest")
                .withNumberOfCommands(3)
                .withGame(brief)
                .withUser(moderator1)
                .build();
        GameSession session3 = new GameSession.GameSessionBuilder("otherUserSession")
                .withUser(moderator2)
                .withGame(brief)
                .build();
        GameSession pastSession = new GameSession.GameSessionBuilder("otherUserSession")
                .withActiveDate(LocalDate.now().minusDays(1))
                .withUser(moderator1)
                .withGame(brief)
                .build();
        GameSession riskMapSession = new GameSession.GameSessionBuilder("riskMap")
                .withUser(moderator1)
                .withGame(riskMap)
                .build();
        gameSessionService.save(session);
        gameSessionService.save(session2);
        gameSessionService.save(session3);
        gameSessionService.save(pastSession);
        gameSessionService.save(riskMapSession);
    }
}
