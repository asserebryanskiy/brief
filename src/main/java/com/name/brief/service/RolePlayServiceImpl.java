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
        String phaseName = game.getPhaseNameByIndex(phaseIndex);
        switch (phaseName) {
            case "SEND_ROLES":
                List<Player> players = game.getGameSession().getPlayers();
                if (players.size() % 2 != 0) throw new OddNumberOfPlayersException();
                game.addPlayers(players);
                sendInstructions(game);
        }

        // change RolePlay phase
        game.setPhaseIndex(phaseIndex);

        // start timer if new phase has it

        // send to subscribers instruction to change to proper phase
        if (phaseName.equals("SURVEY")) {
            game.getPlayersRoles().forEach((id, role) -> {
                if (role == PharmaRole.SALESMAN) {
                    sendToPlayer("changePhase", id,"SURVEY_EXPECTATION");
                } else {
                    sendToPlayer("changePhase", id,"SURVEY");
                }
            });
        } else {
            sendToGame("changePhase", gameId, phaseName);
        }

        // save game
        gameRepository.save(game);
    }

    @Override
    public void nextRound(String instruction, Long gameId) throws WrongGameTypeException {
        // retrieve game
        RolePlay game = getRolePlayGame(gameId);

        // change RolePlay phase
        game.setRoundIndex(game.getRoundIndex() + 1);

        // depending on instruction work with players
        switch (instruction) {
            case "changeRoles":
                game.swapRoles();
                // send to players their new roles and instructions
                sendInstructions(game);
                // change phase
                sendToGame("changePhase", gameId, "SEND_INSTRUCTION");
                break;
            case "nextDoctor":
                game.nextDoctor();
                game.getPlayersRoles().forEach((id, role) -> {
                    if (role == PharmaRole.SALESMAN) {
                        // send crossing message
                        PlayerLocation dto = game.getPlayerLocation(id);
                        sendToPlayer("crossing", id, dto);
                    } else {
                        // send new doctor role
                        InstructionsDto dto = new InstructionsDto(role);
                        sendToPlayer("instructions", id, dto);
                    }
                });
                // change phase
                sendToGame("changePhase", gameId, "CROSSING");
                break;
        }

        // save game
        gameRepository.save(game);
    }

    private void sendToPlayer(String destination, Long playerId, Object payload) {
        template.convertAndSend("/queue/rolePlay/player/" + playerId + "/" + destination, payload);
    }

    private void sendToGame(String destination, Long gameId, Object payload) {
        template.convertAndSend("/topic/game/" + gameId + "/" + destination, payload);
    }

    private void sendInstructions(RolePlay game) {
        game.getPlayersRoles().forEach((id, role) ->
                sendToPlayer("instructions", id, new InstructionsDto(role)));
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
