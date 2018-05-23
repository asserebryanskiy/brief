package com.name.brief.repository;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.Role;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.PlayerData;
import com.name.brief.model.games.roleplay.RolePlay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository repository;

    @Test
    public void whenRetrievingGame_GameSessionIsAlsoRetrieved() {
        Game game = new RolePlay();
        GameSession session = new GameSession.GameSessionBuilder("id")
                .withGame(game)
                .build();
        session.getPlayers().add(new Player(session));

        entityManager.persist(session);

        assertThat(repository.findOne(game.getId()).getGameSession().getPlayers(), hasSize(1));
    }

    @Test
    public void savingRolePlayCreatesDoctorEstimationMapsForItsPlayerData() {
        RolePlay game = new RolePlay();
        game.getPlayersData().add(new PlayerData());

        repository.save(game);

        List<PlayerData> playersData = ((RolePlay) repository.findOne(game.getId())).getPlayersData();
        assertThat(playersData.get(0).getDoctorEstimation().keySet(), hasSize(4));
    }
}