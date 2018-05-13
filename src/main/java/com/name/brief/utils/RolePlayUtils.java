package com.name.brief.utils;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.model.Player;
import com.name.brief.model.games.Phase;
import com.name.brief.model.games.roleplay.*;
import com.name.brief.web.dto.DoctorAnswerDto;
import com.name.brief.web.dto.SalesmanAnswerDto;

import java.util.*;

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

            if (data.getRole() instanceof SalesmanRole) {
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
        assert data.getRole() instanceof SalesmanRole : "new partner could be found only for salesman roles";

        // if list of played players is greater than or equal to total number of doctors, clear it
        if (data.getPlayedPlayers().size() >= playersData.size() / 2)
            data.getPlayedPlayers().clear();

        return playersData.stream()
                .filter(partnerData -> partnerData.getRole() instanceof DoctorRole)
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
            data.setOrderNumber(i + 1);
            Player partner;
            if (i % 2 == 0) {
                partner = players.get(i + 1);
                if (i % 4 == 0) data.setRole(DoctorRole.DOCTOR_1);
                else            data.setRole(DoctorRole.DOCTOR_2);
            } else {
                partner = players.get(i - 1);
                data.setRole(SalesmanRole.SALESMAN_1);
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

    public static void addDoctorAnswers(DoctorAnswerDto dto, PlayerData salesman, int roundIndex) {
        assert salesman.getRole() instanceof SalesmanRole : "Answers could be added only to data with Salesman role";

        Map<SalesmanCompetency, CompetencyResults> current = salesman.getDoctorEstimation();
        Map<String, String> received = dto.getExpertiseEstimations();

        received.forEach((cssClassName, value) -> {
            List<Integer> competencyEstimation = current.get(findCompetency(cssClassName)).getResults();
            competencyEstimation.set(roundIndex, getDoctorEeAnswerIndex(value));
        });

        if (dto.getComment() != null && !dto.getComment().isEmpty())
            salesman.getComments().get(roundIndex).setText(dto.getComment());
    }

    public static void addSalesmanAnswers(SalesmanAnswerDto dto,
                                          PlayerData salesman,
                                          DoctorRole partnerRole,
                                          int roundIndex) {
        assert salesman.getRole() instanceof SalesmanRole : "Answers could be added only to data with Salesman role";

        Map<SalesmanAnswerType, SalesmanAnswersResults> current = salesman.getAnswersAsSalesman();
        Map<String, Integer> received = dto.getAnswers();

        received.forEach((cssClassName, answer) -> {
            SalesmanAnswerType salesmanAnswerType = getSalesmanAnswerType(cssClassName);
            SalesmanAnswersResults salesmanAnswersResults = current.get(salesmanAnswerType);

            // set answer of salesman
            salesmanAnswersResults.getAnswersPerRound().set(roundIndex, answer);

            // set correct answer depending on doctor role
            List<Integer> correctAnswersPerRound = salesmanAnswersResults.getCorrectAnswersPerRound();
            switch (salesmanAnswerType) {
                case NUMBER_OF_PATIENTS:
                    correctAnswersPerRound.set(roundIndex, partnerRole.getPatientsAverage());
                    break;
                case NUMBER_OF_RECIPES:
                    correctAnswersPerRound.set(roundIndex, partnerRole.getRecipeAverage());
                    break;

            }
        });
    }

    private static int getDoctorEeAnswerIndex(String answer) {
        if (answer == null) return 0;

        switch (answer) {
            case "low": return 1;
            case "mid": return 2;
            case "high": return 3;
            default: return 0;
        }
    }

    private static SalesmanCompetency findCompetency(String cssClassName) {
        return Arrays.stream(SalesmanCompetency.values())
                .filter(c -> c.getCssClassName().equals(cssClassName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(cssClassName));
    }

    private static SalesmanAnswerType getSalesmanAnswerType(String cssClassName) {
        return Arrays.stream(SalesmanAnswerType.values())
                .filter(c -> c.getCssClassName().equals(cssClassName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(cssClassName));
    }
}
