package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.Decision;
import com.name.brief.model.GameSession;
import com.name.brief.model.Role;
import com.name.brief.model.User;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.RiskMap;
import com.name.brief.model.games.riskmap.RiskMapType;
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
@Profile({"dev", "localPostgre"})
public class DevDatabaseLoader implements ApplicationRunner {

    private final GameSessionService gameSessionService;
    private final UserRepository userRepository;

    @Autowired
    public DevDatabaseLoader(GameSessionService gameSessionService,
                             UserRepository userRepository) {
        this.gameSessionService = gameSessionService;
        this.userRepository = userRepository;
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
                .withNumberOfCommands(5)
                .withUser(moderator1)
                .build();

        session.getPlayers().get(0).getDecision(0).setAnswer("A1A3");
        session.getPlayers().get(2).getDecision(2).setAnswer("A3A1A4");
        session.getPlayers().get(3).getDecision(3).setAnswer("A3A1A4D1D2");
        session.getPlayers().get(4).getDecision(4).setAnswer("A3A1");

        GameSession session2 = new GameSession.GameSessionBuilder("testtest")
                .withNumberOfCommands(3)
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
                .withUser(moderator1)
                .build();
        ((RiskMap) riskMap.getGame()).setType(RiskMapType.HOTEL);
        gameSessionService.save(session);
        gameSessionService.save(session2);
        gameSessionService.save(session3);
        gameSessionService.save(pastSession);
        gameSessionService.save(riskMap);
    }
}
