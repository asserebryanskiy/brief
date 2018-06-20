package com.name.brief;

import com.name.brief.config.ModeratorSecurityConfig;
import com.name.brief.model.User;
import com.name.brief.repository.GameSessionRepository;
import com.name.brief.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("heroku")
public class HerokuDatabaseLoader implements ApplicationRunner{

    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;

    @Autowired
    public HerokuDatabaseLoader(UserRepository userRepository,
                                GameSessionRepository gameSessionRepository) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String username = "masterskaya";
        if (userRepository.findByUsername(username) == null) {
            String password = ModeratorSecurityConfig.passwordEncoder.encode("slozhniyparol");
            userRepository.save(new User(username, password, "ROLE_MODERATOR"));
        }

        gameSessionRepository.deleteAll();
    }
}
