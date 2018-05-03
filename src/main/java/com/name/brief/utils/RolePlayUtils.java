package com.name.brief.utils;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.model.Player;
import com.name.brief.model.games.Phase;
import com.name.brief.model.games.roleplay.PharmaRole;
import com.name.brief.model.games.roleplay.PlayerData;
import com.name.brief.model.games.roleplay.PlayerLocation;
import com.name.brief.model.games.roleplay.RolePlay;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RolePlayUtils {
    public static PlayerData findPlayerData(Long playerId, List<PlayerData> playersData) {
        return playersData.stream()
                .filter(data -> data.getPlayer().getId().equals(playerId))
                .findAny()
                .orElse(null);
    }

    public static void setNextPartnerForEachPlayer(RolePlay game) {
        Set<Long> occupiedDoctorsIds = new HashSet<>();
        game.getPlayersData().forEach(data -> {
            // add current partner to played players list
            data.getPlayedPlayers().add(data.getCurrentPartnerId());

            if (data.getRole() == PharmaRole.SALESMAN) {
                // find new partner for this player
                PlayerData newPartner = findNewPartner(occupiedDoctorsIds, game.getPlayersData(), data);

                // set him as current partner
                Long partnerId = newPartner.getPlayer().getId();
                data.setCurrentPartnerId(partnerId);

                // set newPartner's current partner
                newPartner.setCurrentPartnerId(data.getPlayer().getId());

                // add it to occupied doctors ids
                occupiedDoctorsIds.add(partnerId);
            }
        });
    }

    public static PlayerData findNewPartner(Set<Long> occupiedDoctorsIds,
                                      List<PlayerData> playersData,
                                      PlayerData data) {
        assert data.getRole() == PharmaRole.SALESMAN : "new partner could be found only for salesman roles";

        // if list of played players is greater than or equal to total number of doctors, clear it
        if (data.getPlayedPlayers().size() >= playersData.size() / 2)
            data.getPlayedPlayers().clear();

        return playersData.stream()
                .filter(partnerData -> partnerData.getRole().isDoctorRole())
                .filter(partnerData -> !occupiedDoctorsIds.contains(partnerData.getPlayer().getId()))
                .filter(partnerData -> !data.getPlayedPlayers().contains(partnerData.getPlayer().getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("All doctors are occupied"));
    }

    public static void addPlayers(List<Player> players, RolePlay game) throws OddNumberOfPlayersException {
        if (players.size() % 2 != 0) throw new OddNumberOfPlayersException();
        // create PlayerData objects for every player
        for (int i = 0; i < players.size(); i++) {
            PlayerData data = new PlayerData(players.get(i));
            Player partner;
            if (i % 2 == 0) {
                partner = players.get(i + 1);
                data.setRole(PharmaRole.SALESMAN);
            } else {
                partner = players.get(i - 1);
                data.setRole(PharmaRole.DOCTOR_GOOD);
            }
            data.setCurrentPartnerId(partner.getId());
            data.getPlayedPlayers().add(partner.getId());
            data.setLocation(new PlayerLocation(0, i / 2));
            game.getPlayersData().add(data);
        }
    }

    public static String getPhaseNameByIndex(int phaseIndex) {
        return RolePlay.phases.stream()
                .filter(phase -> phase.getOrderIndex() == phaseIndex)
                .map(Phase::getEnglishName)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(phaseIndex)));
    }
}
