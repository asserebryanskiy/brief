package com.name.brief.service;

import com.name.brief.exception.GameNotFoundException;
import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.Player;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.PharmaRole;
import com.name.brief.model.games.roleplay.PlayerLocation;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.repository.GameRepository;
import com.name.brief.web.dto.InstructionsDto;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.name.brief.utils.RolePlayUtils.*;

@Service
public class RolePlayServiceImpl implements RolePlayService {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public RolePlayServiceImpl(GameRepository gameRepository,
                               SimpMessagingTemplate template) {
        this.gameRepository = gameRepository;
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
                addPlayers(players, game);
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
                        .filter(data -> data.getRole() == PharmaRole.SALESMAN)
                        .forEach(data -> {
                            // set partners location to new salesman location
                            PlayerLocation location = findPlayerData(
                                    data.getCurrentPartnerId(), game.getPlayersData()).getLocation();
                            data.setLocation(location);
                            // send new location to player
                            sendToPlayer("crossing", data.getPlayer().getId(), data.getLocation());
                        });
                break;
        }

        // change RolePlay phase
        game.setPhaseIndex(phaseIndex);

        // start timer if new phase has it

        // send to subscribers instruction to change to proper phase
        if (phaseName.startsWith("SURVEY")) {
            game.getPlayersData().forEach(data -> {
                Long playerId = data.getPlayer().getId();
                if (data.getRole() == PharmaRole.SALESMAN) {
                    sendToPlayer("changePhase", playerId,"SURVEY_SALESMAN");
                } else {
                    sendToPlayer("changePhase", playerId,"SURVEY_DOCTOR");
                }
            });
        }

        sendToGameTopic("changePhase", gameId, phaseName);

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
