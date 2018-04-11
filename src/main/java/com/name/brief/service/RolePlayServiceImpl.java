package com.name.brief.service;

import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.repository.GameRepository;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolePlayServiceImpl implements RolePlayService {

    private final GameRepository gameRepository;

    @Autowired
    public RolePlayServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
}
