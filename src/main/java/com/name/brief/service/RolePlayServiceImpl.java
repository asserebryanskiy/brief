package com.name.brief.service;

import com.name.brief.exception.GameNotFoundException;
import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.Player;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import com.name.brief.model.games.roleplay.*;
import com.name.brief.repository.GameRepository;
import com.name.brief.repository.PlayerDataRepository;
import com.name.brief.utils.RolePlayUtils;
import com.name.brief.utils.TimerTaskScheduler;
import com.name.brief.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.name.brief.utils.RolePlayStatisticsUtils.createAverageStatisticsDto;
import static com.name.brief.utils.RolePlayStatisticsUtils.createSalesmanStatisticsDto;
import static com.name.brief.utils.RolePlayUtils.*;

@Service
public class RolePlayServiceImpl implements RolePlayService {

    private final GameRepository gameRepository;
    private final PlayerDataRepository playerDataRepository;
    private final SimpMessagingTemplate template;
    private final TimerTaskScheduler timerScheduler;

    @Autowired
    public RolePlayServiceImpl(GameRepository gameRepository,
                               PlayerDataRepository playerDataRepository,
                               SimpMessagingTemplate template,
                               TimerTaskScheduler timerScheduler) {
        this.gameRepository = gameRepository;
        this.playerDataRepository = playerDataRepository;
        this.template = template;
        this.timerScheduler = timerScheduler;
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
    // ToDo: add some kind of PhaseProcessor interface that will have method process() and different phases will realize it
    public void changePhase(int phaseIndex, Long gameId)
            throws WrongGameTypeException, OddNumberOfPlayersException {
        Phase phase = RolePlay.phases.get(phaseIndex);

        // retrieve game
        RolePlay game = getRolePlayGame(gameId);

        // if current phase has timer, stop it
        if (RolePlay.phases.get(game.getPhaseIndex()).isHasTimer()) {
            timerScheduler.stopTimer(gameId);
        }

        // set all players to not ready
        game.getPlayersData().forEach(data -> data.setReady(false));

        // do phase specific stuff
        String phaseName = phase.getEnglishName();
        switch (phaseName) {
            case "SEND_ROLES":
                List<Player> players = game.getGameSession().getPlayers();
                if (players.size() % 2 != 0) throw new OddNumberOfPlayersException();
                game.getPlayersData().clear();
                addPlayers(players, game);
                sendInstructions(game);
                break;
            case "CROSSING":
                sendCrossingMessage(game);
                break;
            case "CROSSING_2":
                setNextPartnerForEachPlayer(game);
                changePlayersLocation(game);
                sendCrossingMessage(game);
                break;
            case "SURVEY":
            case "SURVEY_2":
                game.getPlayersData().forEach(data ->
                        sendChangePhaseMessageByRole(data, "SURVEY"));
                break;
            case "GAME":
            case "GAME_2":
                game.getPlayersData().forEach(data ->
                        sendChangePhaseMessageByRole(data, "GAME"));
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
        if (phase.isHasTimer()) {
            timerScheduler.setUpTimer(gameId, phase.getTimerDuration());
        }

        // send to subscribers instruction to change to proper phase
        sendToGameTopic("changePhase", gameId, phaseName);

        // save game
        gameRepository.save(game);
    }

    private void sendChangePhaseMessageByRole(PlayerData data, String phaseName) {
        String phase;
        if (data.getRole() instanceof SalesmanRole) {
            phase = phaseName.toUpperCase() + "_SALESMAN";
        } else {
            phase = phaseName.toUpperCase() + "_DOCTOR";
        }
        sendToPlayer("changePhase", data.getPlayer().getId(), phase);
    }

    private void changePlayersLocation(RolePlay game) {
        // change location for each Salesman and send new info to him
        game.getPlayersData().stream()
                .filter(data -> data.getRole() instanceof SalesmanRole)
                .forEach(data -> {
                    // set partners location to new salesman location
                    PlayerLocation location = findPlayerData(
                            data.getCurrentPartnerId(), game.getPlayersData()).getLocation();
                    data.setLocation(location);
                    // send new location to player
                });
    }

    private void sendCrossingMessage(RolePlay game) {
        game.getPlayersData().forEach(data -> {
            sendToPlayer("crossing", data.getPlayer().getId(), data.getLocation());
            sendChangePhaseMessageByRole(data, "CROSSING");
        });

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

    @Override
    public void add30sec(Long gameId) {
        timerScheduler.setUpTimer(gameId, Duration.ofSeconds(30));
    }

    @Override
    public void setPlayerReady(Long playerId, Long gameId) throws WrongGameTypeException {
        // ToDo: remove this ugly search for a playerData id by passing it to the client, using rolePlay API
        Long playerDataId = findPlayerData(playerId, getRolePlayGame(gameId).getPlayersData()).getId();

        PlayerData data = playerDataRepository.findOne(playerDataId);

        data.setReady(true);

        playerDataRepository.save(data);
    }

    private void sendToPlayer(String destination, Long playerId, Object payload) {
        template.convertAndSend("/queue/rolePlay/player/" + playerId + "/" + destination, payload);
    }

    private void sendToGameTopic(String destination, Long gameId, Object payload) {
        template.convertAndSend("/topic/game/" + gameId + "/" + destination, payload);
    }

    private void sendInstructions(RolePlay game) {
        game.getPlayersData().forEach(data ->
                sendToPlayer("instructions",
                                        data.getPlayer().getId(),
                                        new InstructionsDto(data.getRole())));
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
