package com.name.repository;

import com.name.games.GameType;
import com.name.model.Player;
import com.name.model.GameSession;
import com.name.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
        return new GameSession("id", LocalDate.now(), GameType.BRIEF, 5, null);
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
                .withActiveDate(LocalDate.now().minusDays(1)).build();
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
}