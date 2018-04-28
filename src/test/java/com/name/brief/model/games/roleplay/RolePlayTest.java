package com.name.brief.model.games.roleplay;

import com.name.brief.model.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class RolePlayTest {

    private List<Player> players;
    private RolePlay game;

    @Before
    public void setUp() throws Exception {
        players = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setId(3 - i);
            players.add(player);
        }

        game = new RolePlay();
        game.addPlayers(players);
    }

    @Test
    public void addPlayers_sortsReceivedIds() {
        assertThat(game.getAllPlayersIds(), is(new Long[]{0L, 1L, 2L, 3L}));
    }

    @Test
    public void addPlayers_makesEveryEvenPlayerSalesmanNadEveryOddDoctor() {
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                assertThat(game.getPlayersRoles().get(game.getAllPlayersIds()[i]), is(PharmaRole.SALESMAN));
            } else {
                assertThat(game.getPlayersRoles().get(game.getAllPlayersIds()[i]), not(PharmaRole.SALESMAN));
            }
        }
    }

    @Test
    public void addPlayers_createsSubsequentPairsOfPayers() {
        Long[] ids = game.getAllPlayersIds();
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                assertThat(game.getCurrentPairs().get(ids[i]), is(ids[i + 1]));
            } else {
                assertThat(game.getCurrentPairs().get(ids[i]), is(ids[i - 1]));
            }
        }
    }

    @Test
    public void addPlayers_addsLastPlayedDoctorForEverySalesman() {
        for (int i = 0; i < players.size(); i++) {
            Long id = game.getAllPlayersIds()[i];
            if (game.getPlayersRoles().get(id) == PharmaRole.SALESMAN) {
                assertThat(game.getLastPlayedDoctorIndex().get(id), is(i + 1));
            }
        }
    }

    @Test
    public void swapRoles_interchangesRolesOfPlayers() {
        game.swapRoles();

        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                assertThat(game.getPlayersRoles().get(game.getAllPlayersIds()[i]), not(PharmaRole.SALESMAN));
            } else {
                assertThat(game.getPlayersRoles().get(game.getAllPlayersIds()[i]), is(PharmaRole.SALESMAN));
            }
        }
    }

    @Test
    public void nextDoctor_changesDoctorForEverySalesman() {
        game.nextDoctor();

        assertThat(game.getCurrentPairs().get(0L), is(3L));
        assertThat(game.getCurrentPairs().get(2L), is(1L));
        assertThat(game.getCurrentPairs().get(1L), is(2L));
        assertThat(game.getCurrentPairs().get(3L), is(0L));
    }

    @Test
    public void nextDoctor_calledTwiceReturnsToOriginal() {
        game.nextDoctor();
        game.nextDoctor();

        Long[] ids = game.getAllPlayersIds();
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                assertThat(game.getCurrentPairs().get(ids[i]), is(ids[i + 1]));
            } else {
                assertThat(game.getCurrentPairs().get(ids[i]), is(ids[i - 1]));
            }
        }
    }

    @Test
    public void nextDoctor_changesLocationOfSalesmans() {
        game.nextDoctor();

        assertThat(game.getPlayerLocation(0L), is(new PlayerLocation(0, 1)));
        assertThat(game.getPlayerLocation(2L), is(new PlayerLocation(0, 0)));
    }

    @Test
    public void nextDoctor_calledTwiceReturnsLocationToOriginal() {
        game.nextDoctor();
        game.nextDoctor();

        assertThat(game.getPlayerLocation(0L), is(new PlayerLocation(0, 0)));
        assertThat(game.getPlayerLocation(2L), is(new PlayerLocation(0, 1)));
    }

    @Test
    public void nextDoctor_givesDoctorsNewRolesEveryTime() {
        PharmaRole[] roles = PharmaRole.getDoctorRoles();

        for (int i = 0; i < roles.length; i++) {
            game.nextDoctor();
            assertThat(game.getRole(1L), is(roles[(i + 1) % roles.length]));
            assertThat(game.getRole(3L), is(roles[(i + 1) % roles.length]));
        }
    }
}