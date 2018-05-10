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
import java.util.Random;

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
        users.add(moderator1);
        userRepository.save(users);
    }
}
