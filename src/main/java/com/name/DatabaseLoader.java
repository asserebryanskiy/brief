package com.name;

import com.name.config.SecurityConfig;
import com.name.model.GameSession;
import com.name.model.Role;
import com.name.model.User;
import com.name.repository.UserRepository;
import com.name.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {

    private final GameSessionService gameSessionService;
    private final UserRepository userRepository;

    @Autowired
    public DatabaseLoader(GameSessionService gameSessionService,
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
        GameSession session = new GameSession.GameSessionBuilder("test")
                .withNumberOfCommands(5)
                .withUser(moderator1)
                .build();
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
        session.getPlayers().get(1).setCurrentAnswer("1A,3B");
        gameSessionService.save(session);
        gameSessionService.save(session2);
        gameSessionService.save(session3);
        gameSessionService.save(pastSession);
    }
}
