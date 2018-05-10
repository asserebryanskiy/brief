package com.name.brief;

import com.name.brief.config.SecurityConfig;
import com.name.brief.model.Role;
import com.name.brief.model.User;
import com.name.brief.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("vm")
public class VmDatabaseLoader implements ApplicationRunner {
    private final UserRepository userRepository;

    public VmDatabaseLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String username = "test";
        if (userRepository.findByUsername(username) == null) {
            String password = SecurityConfig.passwordEncoder.encode("test");
            userRepository.save(new User(username, password, "ROLE_MODERATOR"));
        }
    }
}
