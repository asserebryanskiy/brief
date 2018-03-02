package com.name.model;

import com.name.games.GameType;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class GameSessionTest {
    @Test
    public void onConstructionCreatesListOfCommands() {
        GameSession session = new GameSession("unique", LocalDate.now(), GameType.BRIEF, 10, null);

        assertThat(session.getPlayers(), hasItems(isA(Player.class)));
        assertThat(session.getPlayers(), hasSize(10));
    }

}