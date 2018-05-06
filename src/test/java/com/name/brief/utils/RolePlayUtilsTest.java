package com.name.brief.utils;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.model.Player;
import com.name.brief.model.games.roleplay.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.name.brief.utils.RolePlayUtils.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class RolePlayUtilsTest {

    @Test
    public void findPlayersData_returnsCorrectPlayerDataIfIdIsFound() {
        List<PlayerData> playersData = createPlayersData(2);

        assertThat(findPlayerData(0L, playersData).getPlayer().getId(), is(0L));
    }

    @Test
    public void findPlayersData_returnsNullIfIdIsNotFound() {
        List<PlayerData> playersData = createPlayersData(2);

        assertThat(findPlayerData(2L, playersData), nullValue());
    }

    @Test
    public void addPlayers_createsPlayerDataObjectForEveryPlayer() throws OddNumberOfPlayersException {
        RolePlay game = new RolePlay();

        addPlayers(createPlayers(2), game);

        assertThat(game.getPlayersData().size(), is(2));
    }

    @Test(expected = OddNumberOfPlayersException.class)
    public void addPlayers_throwsExceptionIfTryingToAddOddNumberOfPlayers() throws OddNumberOfPlayersException {
        addPlayers(createPlayers(3), new RolePlay());
    }

    @Test
    public void addPlayers_addsCorrectPartnerForEveryPlayer() throws OddNumberOfPlayersException {
        RolePlay game = new RolePlay();

        for (int i = 2; i < 20; i += 2) {
            addPlayers(createPlayers(i), game);

            for (int j = 0; j < i; j++) {
                if (j % 2 == 0) {
                    assertThat(game.getPlayersData().get(j).getCurrentPartnerId(), is((long) (j + 1)));
                } else {
                    assertThat(game.getPlayersData().get(j).getCurrentPartnerId(), is((long) (j - 1)));
                }
            }

            game.getPlayersData().clear();
        }
    }

    @Test
    public void addPLayers_setsCorrectRolesForEveryPlayer() throws OddNumberOfPlayersException {
        RolePlay game = new RolePlay();

        for (int i = 2; i < 20; i += 2) {
            addPlayers(createPlayers(i), game);

            for (int j = 0; j < i; j++) {
                if (j % 2 == 0) {
                    if (j % 4 == 0) {
                        assertThat(game.getPlayersData().get(j).getRole(), is(DoctorRole.DOCTOR_2));
                    } else {
                        assertThat(game.getPlayersData().get(j).getRole(), is(DoctorRole.DOCTOR_1));
                    }
                } else {
                    assertThat(game.getPlayersData().get(j).getRole(), is(SalesmanRole.SALESMAN_1));
                }
            }

            game.getPlayersData().clear();
        }
    }

    @Test
    public void addPlayers_addsCorrectLocationForEveryPlayer() throws OddNumberOfPlayersException {
        RolePlay game = new RolePlay();

        for (int i = 2; i < 20; i += 2) {
            addPlayers(createPlayers(i), game);

            for (int j = 0; j < i; j++) {
                assertThat(game.getPlayersData().get(j).getLocation().getHospital(), is(0));
                assertThat(game.getPlayersData().get(j).getLocation().getRoom(), is(j / 2));
            }

            game.getPlayersData().clear();
        }
    }

    @Test(expected = AssertionError.class)
    public void findNewPartner_notAllowsPlayerDataWithDoctorRole() {
        PlayerData data = new PlayerData();
        data.setRole(DoctorRole.DOCTOR_1);
        findNewPartner(new HashSet<>(), new ArrayList<>(), data);
    }

    @Test
    public void findNewPartner_returnsNotOccupiedDoctor() {
        Set<Long> occupiedDoctors = new HashSet<>();
        occupiedDoctors.add(0L);
        List<PlayerData> playersData = createPlayersData(4);

        assertThat(findNewPartner(occupiedDoctors, playersData, playersData.get(1)).getPlayer().getId(),
                is(2L));
    }

    @Test
    public void findNewPartner_returnsNotPlayedDoctor() {
        List<PlayerData> playersData = createPlayersData(4);
        PlayerData data = playersData.get(1);
        data.getPlayedPlayers().add(0L);

        assertThat(findNewPartner(new HashSet<>(), playersData, data).getPlayer().getId(),
                is(2L));
    }

    @Test
    public void findNewPartner_returnsNotPlayedAndNotOccupiedDoctor() {
        List<PlayerData> playersData = createPlayersData(6);
        PlayerData data = playersData.get(1);
        data.getPlayedPlayers().add(0L);
        HashSet<Long> occupiedDoctorsIds = new HashSet<>();
        occupiedDoctorsIds.add(2L);

        assertThat(findNewPartner(occupiedDoctorsIds, playersData, data).getPlayer().getId(),
                is(4L));
    }

    @Test
    public void findNewPartner_clearsPlayedPlayersListIfItIsOverwhelmed() {
        List<PlayerData> playersData = createPlayersData(4);
        PlayerData data = playersData.get(1);
        data.getPlayedPlayers().add(0L);
        data.getPlayedPlayers().add(2L);

        findNewPartner(new HashSet<>(), playersData, data);

        assertThat(data.getPlayedPlayers(), hasSize(0));
    }

    @Test
    public void setNextPartnerForEachPlayer_worksCorrectly() throws OddNumberOfPlayersException {
        RolePlay game = new RolePlay();
        addPlayers(createPlayers(4), game);

        setNextPartnerForEachPlayer(game);

        assertThat(game.getPlayersData().get(0).getCurrentPartnerId(), is(3L));
        assertThat(game.getPlayersData().get(1).getCurrentPartnerId(), is(2L));
        assertThat(game.getPlayersData().get(2).getCurrentPartnerId(), is(1L));
        assertThat(game.getPlayersData().get(3).getCurrentPartnerId(), is(0L));
    }

    private List<PlayerData> createPlayersData(int size) {
        List<PlayerData> playersData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            player.setId((long) i);
            PlayerData data = new PlayerData(player);
            data.setRole(i % 2 == 0 ? DoctorRole.DOCTOR_1 : SalesmanRole.SALESMAN_1);
            playersData.add(data);
        }
        return playersData;
    }

    private List<Player> createPlayers(int size) {
        List<Player> players = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            player.setId(i);
            players.add(player);
        }
        return players;
    }
}