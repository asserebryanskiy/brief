package com.name.brief.repository;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.User;
import com.name.brief.model.games.Brief;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameSessionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameSessionRepository repository;

    @TestConfiguration
    static class Configuration {
        @Bean
        public EvaluationContextExtension securityExtension() {
            return new EvaluationContextExtensionSupport() {
                @Override
                public String getExtensionId() {
                    return "security";
                }

                @Override
                public Object getRootObject() {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    return new SecurityExpressionRoot(authentication) {};
                }
            };
        }
    }

    private GameSession createSession() {
        return new GameSession("id", LocalDate.now(), new Brief(), 5, null);
    }

    @Test
    public void update_withUpdatedCommandPersistsCommandAsWell() {
        GameSession session = createSession();
        entityManager.persist(session);
        entityManager.flush();

        Player player = session.getPlayers().get(0);
        player.setLoggedIn(true);
        repository.save(session);
        GameSession found = repository.findOne(session.getId());

        assertThat(found.getPlayers().get(0).isLoggedIn(), is(true));
    }

    @Test
    public void getSessionByStrIdAndActiveDate_returnsNullIfStrIdMatchAndActiveDateNot() {
        GameSession session = createSession();
        entityManager.persist(session);
        entityManager.flush();

        GameSession found = repository.findByStrIdAndActiveDate("id", LocalDate.now().minusDays(1));

        assertThat(found, nullValue());
    }

    @Test
    public void whenFindByStringIdAndDate_returnsGameSession() {
        GameSession session = createSession();
        entityManager.persist(session);
        entityManager.flush();

        GameSession found = repository.findByStrIdAndActiveDate("id", LocalDate.now());

        assertThat(found.getStrId(), is(session.getStrId()));
    }

    @Test
    public void findBuStrIdAndActiveDate_returnsNullIfNoValidSessionFound() {
        assertThat(repository.findByStrIdAndActiveDate("id", LocalDate.now()), nullValue());
    }

    @Test
    public void findBuStrIdAndActiveDate_returnsNullIfSessionActiveDateExpired() {
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withActiveDate(LocalDate.now().minusDays(1))
                .withUser(new User())
                .build();
        entityManager.persist(session);
        entityManager.flush();

        GameSession found = repository.findByStrIdAndActiveDate("id", LocalDate.now());

        assertThat(found, nullValue());
    }

    @Test
    public void savingSessionAddsIdToIt() {
        GameSession session = createSession();

        repository.save(session);

        assertThat(session.getId(), notNullValue());
    }

    @Test
    public void findSessionsAfter_ReturnsOnlySessionsOfCurrentUser() {
        User user1 = new User("user1", "", "");
        User user2 = new User("user2", "", "");
        entityManager.persist(user1);
        entityManager.persist(user2);
        IntStream.range(0, 10).forEach(i -> {
            GameSession session = new GameSession.GameSessionBuilder(String.valueOf(i))
                    .withUser(i < 5 ? user1 : user2)
                    .withActiveDate(LocalDate.now().plusDays(i))
                    .build();
            entityManager.persist(session);
        });
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user1, null));
        SecurityContextHolder.getContext().getAuthentication();

        List<GameSession> gameSessions = repository.findSessionsAfter(LocalDate.now());

        assertThat(gameSessions, hasSize(5));
    }

    @Test
    public void changePhaseWorksAsExpected() {
        GameSession session = createDefaultSession();
        repository.save(session);

        repository.changePhase(session.getId(), 2);

        assertThat(repository.findOne(session.getId()).getCurrentPhaseNumber(), is(2));
    }

    @Test
    public void changePhaseDoesNothingIfIdDoesNotExist() {
        repository.changePhase(0L, 2);
    }

    @Test
    public void findCurrentPhaseNumberById_returnsProperPhase() {
        GameSession session = createDefaultSession();
        session.setCurrentPhaseNumber(2);
        repository.save(session);

        assertThat(repository.findCurrentPhaseNumberById(session.getId()).getCurrentPhaseNumber(),
                is(2));
    }

    @Test
    public void setEndOfTimerWorksAsExpected() {
        GameSession session = createDefaultSession();
        repository.save(session);

        LocalTime time = LocalTime.of(20, 20);
        repository.setEndOfTimer(session.getId(), time);

        assertThat(repository.findOne(session.getId()).getEndOfTimer(), is(time));
    }

    private GameSession createDefaultSession() {
        User user = new User("user", "", "ROLE_MODERATOR");
        entityManager.persist(user);
        return new GameSession.GameSessionBuilder("id")
                .withUser(user)
                .build();
    }
}