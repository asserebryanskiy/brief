package com.name.brief.model.games.roleplay;

import com.name.brief.exception.NoPlayersAddedException;
import com.name.brief.model.BaseEntity;
import com.name.brief.model.Decision;
import com.name.brief.model.Player;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "roleplay")
@Data
public class RolePlay extends Game {
    private final int numberOfRounds = 1;
    private final String russianName = "Ролевая игра";
    private final String englishName = "rolePLay";

    private final String[] strategies = {
            "Доктор - Медицинский представитель"
    };

    @OrderColumn
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    // sorted list of all players ids
    private Long[] allPlayersIds;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Map<Long, Integer> playersScoreMap;

    @ElementCollection(fetch = FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    // map that contains for every playerId id every DoctorRole that they played
    private Map<Long, DoctorRolesSet> playedDoctorsRoles;

    @ElementCollection(fetch = FetchType.EAGER)
    @Setter(AccessLevel.PROTECTED)
    // map that contains for every playerId id their current role
    private Map<Long, PharmaRole> playersRoles;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    // map that contains for every playerId id index in allPlayersIds list of last played player
    private Map<Long, Integer> lastPlayedDoctorIndex;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    // map that contains for every playerId id of his current partner
    private Map<Long, Long> currentPairs;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    // map that contains for every playerId PlayerLocation object
    private Map<Long, PlayerLocation> playersLocation;

    private int strategyNumber;
    private int phaseIndex;
    private int roundIndex;

    public RolePlay() {
        super();
    }

    public RolePlay(int numberOfCommands) {
        this();
        this.playersScoreMap = new HashMap<>(numberOfCommands);
    }

    public void addPlayers(List<Player> players) {
        // initialize data structures
        playersScoreMap = new HashMap<>(players.size());
        playedDoctorsRoles = new HashMap<>(players.size());
        playersRoles = new HashMap<>(players.size());
        lastPlayedDoctorIndex = new HashMap<>(players.size());
        currentPairs = new HashMap<>(players.size());
        playersLocation = new HashMap<>(players.size());

        // create all players ids and sort it
        allPlayersIds = players.stream()
                .map(Player::getId)
                .sorted()
                .toArray(Long[]::new);

        // initialize playedDoctorsRoles
        for (int i = 0; i < players.size(); i++)
            playedDoctorsRoles.put(allPlayersIds[i], new DoctorRolesSet());

        // distribute players to roles and pairs
        for (int i = 0; i < players.size(); i++) {
            // all even players will have role SALESMAN
            Long id = allPlayersIds[i];
            if (i % 2 == 0) {
                playersRoles.put(id, PharmaRole.SALESMAN);
                currentPairs.put(id, allPlayersIds[i + 1]);
                lastPlayedDoctorIndex.put(id, i + 1);
            } else {
                playersRoles.put(id, getNotPlayedDoctorRole(id));
                currentPairs.put(id, allPlayersIds[i - 1]);
            }
            playersLocation.put(id, new PlayerLocation(0, i / 2));
        }
    }

    @Override
    public List<Phase> getPhases() {
        List<Phase> phases;
        if (roundIndex == 0) {
            phases = new ArrayList<>(7);
            phases.addAll(Arrays.asList(
                    new Phase("Формирование игры","FORM_GAME", false),
                    new Phase("Подключение участников","CONNECT_PLAYERS", false),
                    new Phase("Распределение ролей","SEND_ROLES", false),
                    new Phase("Инструкция","SEND_INSTRUCTION", false),
                    new Phase("Переход участников","CROSSING", false),
                    new Phase("Игра", "GAME", true, Duration.ofSeconds(300)),
                    new Phase("Анкета", "SURVEY", false)
            ));
            for (int i = 0; i < phases.size(); i++) {
                phases.get(i).setId(i);
            }
        } else {
            phases = new ArrayList<>(4);
            phases.addAll(Arrays.asList(
                    new Phase("Переход участников","CROSSING", false),
                    new Phase("Инструкция","SEND_INSTRUCTION", false),
                    new Phase("Игра", "GAME", true, Duration.ofSeconds(300)),
                    new Phase("Анкета", "SURVEY", false)
            ));
            for (int i = 0; i < phases.size(); i++) {
                // cause first three phases were deleted after round 0 in front end
                phases.get(i).setId(i + 3);
            }
        }

        return phases;
    }

    @Override
    public List<Phase> getPhases(int roundNumber) {
        return null;
    }

    @Override
    public int getScore(Decision decision) {
        return 0;
    }

    @Override
    public String getCorrectAnswer(int numberOfRound) {
        return null;
    }

    @Override
    public Object getCorrectAnswers() {
        return null;
    }

    @Override
    public Object getAnswerInput(Decision decision) {
        return null;
    }

    public void setStrategyNumber(int strategyNumber) {
        this.strategyNumber = strategyNumber;
    }

    public void swapRoles() {
        if (allPlayersIds == null) throw new NoPlayersAddedException();

        playersRoles.forEach((playerId, role) -> {
            if (role == PharmaRole.SALESMAN) {
                playersRoles.put(playerId, getNotPlayedDoctorRole(playerId));
            } else {
                playersRoles.put(playerId, PharmaRole.SALESMAN);
            }
        });

        // clear last played indices
        lastPlayedDoctorIndex.clear();
    }

    /**
     * For every SALESMAN finds and applies a Doctor with whom he has not played
     */
    public void nextDoctor() {
        if (allPlayersIds == null) throw new NoPlayersAddedException();

        boolean[] occupiedDoctorIndices = new boolean[allPlayersIds.length];

        // for every salesman
        Arrays.stream(allPlayersIds)
                .forEach(id -> {
                    if (playersRoles.get(id) == PharmaRole.SALESMAN) {
                        int counter = lastPlayedDoctorIndex.get(id) + 1;
                        for (int i = 0; i < allPlayersIds.length / 2; i++) {
                            if (counter >= allPlayersIds.length) counter = 0;
                            Long foundId = allPlayersIds[counter];
                            if (!occupiedDoctorIndices[counter] // this doctor is not assigned to anybody
                                    && !foundId.equals(id)      // foundId is not an id of currently processed player
                                    // found player has DOCTOR role
                                    && playersRoles.get(foundId) != PharmaRole.SALESMAN) {
                                // change current pairs for both players
                                currentPairs.put(id, foundId);
                                currentPairs.put(foundId, id);

                                // change location for SALESMAN
                                playersLocation.put(id, playersLocation.get(foundId));

                                // change last played index
                                lastPlayedDoctorIndex.put(id, counter);

                                // mark doctor as occupied
                                occupiedDoctorIndices[counter] = true;
                                break;
                            }
                            counter++;
                        }
                    } else {
                        playersRoles.put(id, getNotPlayedDoctorRole(id));
                    }
                });
    }

    public String getPhaseNameByIndex(int phaseIndex) {
        return getPhases().stream()
                .filter(phase -> phase.getId() == phaseIndex)
                .map(Phase::getEnglishName)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(phaseIndex)));
    }

    private PharmaRole getNotPlayedDoctorRole(Long id) {
        Set<PharmaRole> playedRoles = playedDoctorsRoles.get(id).getRoles();
        PharmaRole[] availableRoles = Arrays.stream(PharmaRole.values())
                .filter(PharmaRole::isDoctorRole)
                .toArray(PharmaRole[]::new);
        if (playedRoles.size() == availableRoles.length) playedRoles.clear();
        PharmaRole newRole = Arrays.stream(availableRoles)
                .filter(role -> !playedRoles.contains(role))
                .findAny()
                // could not be null because we checked that playedRoles are smaller than available roles
                .orElse(null);
        playedRoles.add(newRole);

        return newRole;
    }

    public PlayerLocation getPlayerLocation(Long id) {
        return playersLocation.get(id);
    }

    public Object getRole(Long playerId) {
        return playersRoles == null ? PharmaRole.SALESMAN : playersRoles.get(playerId);
    }
}
