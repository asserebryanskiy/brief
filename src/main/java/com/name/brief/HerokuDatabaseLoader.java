package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.User;
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

    @Autowired
    public HerokuDatabaseLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String password = SecurityConfig.passwordEncoder.encode("slozhniyparol");
        userRepository.save(new User("masterskaya", password, "ROLE_MODERATOR"));
    }
}
