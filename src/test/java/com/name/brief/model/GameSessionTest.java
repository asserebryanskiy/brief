package com.name.brief.model;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.Brief;
import com.name.brief.web.dto.StatsList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class GameSessionTest {

    private GameSession session;

    @Before
    public void setUp() throws Exception {
        session = new GameSession.GameSessionBuilder("id").build();
        for (int i = 0; i < 3; i++) {
            session.getPlayers().add(new Player(session));
        }
    }

    @Test
    public void getStatsListOnPlayersWithMaxResultReturnsProperList() {
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer("A3")));

        StatsList stats = session.getStatsList();

        assertThat(stats.getStatistics(), hasSize(2));
        stats.getStatistics().forEach(s -> {
            assertThat(s.getRoundScoreMap().keySet(), hasSize(session.getGame().getNumberOfRounds()));
//            assertThat(s.getRoundScoreMap().values(), everyItem(is(15)));
        });
    }

    @Test
    public void getStatsListOnPlayersWithMinResultReturnsProperList() {
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer("")));

        StatsList stats = session.getStatsList();

        assertThat(stats.getStatistics(), hasSize(2));
        stats.getStatistics().forEach(s -> {
            assertThat(s.getRoundScoreMap().keySet(), hasSize(session.getGame().getNumberOfRounds()));
            assertThat(s.getRoundScoreMap().values(), everyItem(is(0)));
        });
    }

    @Test
    public void getStatsListOnPlayersWithMiddleResultReturnsProperList() {
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer("A3B2")));

        StatsList stats = session.getStatsList();

        assertThat(stats.getStatistics(), hasSize(2));
        stats.getStatistics().forEach(s -> {
            assertThat(s.getRoundScoreMap().keySet(), hasSize(session.getGame().getNumberOfRounds()));
//            assertThat(s.getRoundScoreMap().values(), everyItem(is(10)));
        });
    }
}