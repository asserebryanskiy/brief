package com.name.brief.service;

import com.name.brief.exception.GameNotFoundException;
import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.Player;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.*;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.PlayerDataRepository;
import com.name.brief.utils.RolePlayUtils;
import com.name.brief.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.name.brief.utils.RolePlayStatisticsUtils.createAverageStatisticsDto;
import static com.name.brief.utils.RolePlayStatisticsUtils.createSalesmanStatisticsDto;
import static com.name.brief.utils.RolePlayUtils.*;

@Service
public class RolePlayServiceImpl implements RolePlayService {

    private final GameRepository gameRepository;
    private final PlayerDataRepository playerDataRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public RolePlayServiceImpl(GameRepository gameRepository,
                               PlayerDataRepository playerDataRepository,
                               SimpMessagingTemplate template) {
        this.gameRepository = gameRepository;
        this.playerDataRepository = playerDataRepository;
        this.template = template;
    }

    @Override
    public void setUp(Long gameId, RolePlaySettingsDto dto) throws WrongGameTypeException {
        Game game = gameRepository.findOne(gameId);
        if (game instanceof RolePlay) {
            RolePlay rolePlay = (RolePlay) game;
            rolePlay.setStrategyNumber(dto.getStrategy());
            gameRepository.save(rolePlay);
        } else {
            throw new WrongGameTypeException(game == null ? null : game.getClass().getSimpleName(),
                    RolePlay.class.getSimpleName());
        }
    }

    @Override
    public void changePhase(int phaseIndex, Long gameId)
            throws WrongGameTypeException, OddNumberOfPlayersException {
        // retrieve game
        RolePlay game = getRolePlayGame(gameId);

        // do phase specific stuff
        String phaseName = getPhaseNameByIndex(phaseIndex);
        switch (phaseName) {
            case "SEND_ROLES":
                List<Player> players = game.getGameSession().getPlayers();
                if (players.size() % 2 != 0) throw new OddNumberOfPlayersException();
                if (game.getPlayersData().size() < players.size()) addPlayers(players, game);
                sendInstructions(game);
                break;
            case "CROSSING":
                game.getPlayersData().forEach(data ->
                        sendToPlayer("crossing", data.getPlayer().getId(), data.getLocation()));
                break;
            case "CROSSING_2":
                setNextPartnerForEachPlayer(game);

                // change location for each Salesman and send new info to him
                game.getPlayersData().stream()
                        .filter(data -> data.getRole() instanceof SalesmanRole)
                        .forEach(data -> {
                            // set partners location to new salesman location
                            PlayerLocation location = findPlayerData(
                                    data.getCurrentPartnerId(), game.getPlayersData()).getLocation();
                            data.setLocation(location);
                            // send new location to player
                            sendToPlayer("crossing", data.getPlayer().getId(), data.getLocation());
                        });
                break;
            case "SURVEY":
            case "SURVEY_2":
                game.getPlayersData().forEach(data -> {
                    Long playerId = data.getPlayer().getId();
                    if (data.getRole() instanceof SalesmanRole) {
                        sendToPlayer("changePhase", playerId,"SURVEY_SALESMAN");
                    } else {
                        sendToPlayer("changePhase", playerId,"SURVEY_DOCTOR");
                    }
                });
                break;
            case "DRUGS_DISTRIBUTION":
                game.getPlayersData().forEach(data -> {
                    if (data.getRole() instanceof DoctorRole) {
                        sendToPlayer("changePhase", data.getPlayer().getId(),"DRUGS_DISTRIBUTION_SURVEY");
                    } else {
                        sendToPlayer("changePhase", data.getPlayer().getId(), "EXPECTATION");
                    }
                });
                break;
            case "RESULTS":
                game.getPlayersData().forEach(data -> {
                    Long playerId = data.getPlayer().getId();
                    if (data.getRole() instanceof DoctorRole) {
                        sendToPlayer("changePhase", playerId,"RESULTS_AVERAGE");
                        sendToPlayer("averageResults", playerId, createAverageStatisticsDto(game));
                    } else {
                        sendToPlayer("yourResults", playerId, createSalesmanStatisticsDto(data));
                        sendToPlayer("changePhase", playerId, "RESULTS_SALESMAN");
                    }
                });
                break;
        }

        // change RolePlay phase
        game.setPhaseIndex(phaseIndex);

        // start timer if new phase has it

        // send to subscribers instruction to change to proper phase
        sendToGameTopic("changePhase", gameId, phaseName);

        // save game
        gameRepository.save(game);
    }

    @Override
    public void saveDoctorAnswers(Long gameId, DoctorAnswerDto dto, Long playerId) throws WrongGameTypeException, OddNumberOfPlayersException {
        // find game
        RolePlay game = getRolePlayGame(gameId);

        /*
        * DEBUG ONLY!
        * */
        if (game.getPlayersData().isEmpty()) addPlayers(game.getGameSession().getPlayers(), game);

        // change provided player's partner results
        Long partnerId = findPlayerData(playerId, game.getPlayersData()).getCurrentPartnerId();
        PlayerData partner = findPlayerData(partnerId, game.getPlayersData());
        RolePlayUtils.addDoctorAnswers(dto, partner, game.getPhaseIndex() == 6 ? 0 : 1);

        // save updated partner
        playerDataRepository.save(partner);
    }

    @Override
    public void saveSalesmanAnswers(Long gameId, SalesmanAnswerDto dto, Long playerId) throws WrongGameTypeException {
        // find game
        RolePlay game = getRolePlayGame(gameId);

        // change provided player's partner results
        PlayerData playerData = findPlayerData(playerId, game.getPlayersData());
        DoctorRole partnerRole = (DoctorRole) findPlayerData(playerData.getCurrentPartnerId(), game.getPlayersData())
                .getRole();
        RolePlayUtils.addSalesmanAnswers(dto, playerData, partnerRole, game.getPhaseIndex() == 6 ? 0 : 1);

        // save updated playerData
        playerDataRepository.save(playerData);
    }

    @Override
    public void saveDrugDistribution(Long gameId, DrugDistributionDto dto, Long playerId) throws WrongGameTypeException {
        // find game
        RolePlay game = getRolePlayGame(gameId);
        PlayerData player = findPlayerData(playerId, game.getPlayersData());

        // add to current partner as second round
        PlayerData currentPartner = findPlayerData(player.getCurrentPartnerId(), game.getPlayersData());
        currentPartner.getAnswersAsSalesman().get(SalesmanAnswerType.SELL_FORECAST)
                .getCorrectAnswersPerRound().set(1, dto.getDrugPackages()[1]);

        // add to previous partner as first round
        PlayerData previousPartner = findPlayerData(player.getPlayedPlayers().iterator().next(), game.getPlayersData());
        previousPartner.getAnswersAsSalesman().get(SalesmanAnswerType.SELL_FORECAST)
                .getCorrectAnswersPerRound().set(0, dto.getDrugPackages()[0]);

        // save game
        gameRepository.save(game);
    }

    private void sendToPlayer(String destination, Long playerId, Object payload) {
        template.convertAndSend("/queue/rolePlay/player/" + playerId + "/" + destination, payload);
    }

    private void sendToGameTopic(String destination, Long gameId, Object payload) {
        template.convertAndSend("/topic/game/" + gameId + "/" + destination, payload);
    }

    private void sendInstructions(RolePlay game) {
        game.getPlayersData().forEach(data ->
                sendToPlayer("instructions", data.getPlayer().getId(), new InstructionsDto(data.getRole())));
    }

    private RolePlay getRolePlayGame(Long gameId) throws WrongGameTypeException {
        RolePlay game;
        Game found = gameRepository.findOne(gameId);
        if (found == null) throw new GameNotFoundException(gameId);
        try {
            game = (RolePlay) found;
        } catch (ClassCastException e) {
            throw new WrongGameTypeException(found.getClass().getSimpleName(), "RolePlay");
        }
        return game;
    }
}
