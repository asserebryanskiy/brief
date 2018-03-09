package com.name.brief.model;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.Brief;
import com.name.brief.web.dto.StatsList;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class GameSessionTest {
    @Test
    public void onConstructionCreatesListOfCommands() {
        GameSession session = new GameSession("unique", LocalDate.now(), new Brief(), 10, null);

        assertThat(session.getPlayers(), hasItems(isA(Player.class)));
        assertThat(session.getPlayers(), hasSize(10));
    }

    @Test
    public void getStatsListOnPlayersWithMaxResultReturnsProperList() {
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withNumberOfCommands(2)
                .build();
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer("A1")));

        StatsList stats = session.getStatsList();

        assertThat(stats.getStatistics(), hasSize(2));
        stats.getStatistics().forEach(s -> {
            assertThat(s.getRoundScoreMap().keySet(), hasSize(session.getGame().getNumberOfRounds()));
            assertThat(s.getRoundScoreMap().values(), everyItem(is(3)));
        });
    }

    @Test
    public void getStatsListOnPlayersWithMinResultReturnsProperList() {
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withNumberOfCommands(2)
                .build();
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
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withNumberOfCommands(2)
                .build();
        session.getPlayers().forEach(p -> p.getDecisions().forEach(d -> d.setAnswer("A1B2")));

        StatsList stats = session.getStatsList();

        assertThat(stats.getStatistics(), hasSize(2));
        stats.getStatistics().forEach(s -> {
            assertThat(s.getRoundScoreMap().keySet(), hasSize(session.getGame().getNumberOfRounds()));
            assertThat(s.getRoundScoreMap().values(), everyItem(is(2)));
        });
    }
}