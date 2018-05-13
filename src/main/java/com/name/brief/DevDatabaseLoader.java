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

//        addBriefWithAnswers(moderator1);
    }

    private void addBriefWithAnswers(User moderator1) {
        GameSession brief = new GameSession.GameSessionBuilder("brief")
                .withGame(new Brief())
                .withUser(moderator1)
                .build();
        brief.setCurrentPhaseNumber(5);

        String[] correctAnswers = (String[]) brief.getGame().getCorrectAnswers();
        String[] addition = new String[]{"","A4","A4B4","A4B4C4","A4B4C4A2"};

        for (int i = 0; i < brief.getGame().getNumberOfRounds(); i++) {
            final int ind = i;
            brief.getPlayers().forEach(p -> {
                p.getDecision(ind).setAnswer(correctAnswers[ind]
                        + addition[new Random().nextInt(correctAnswers.length)]);
            });
        }

        gameSessionService.save(brief);
    }
}
