package com.name.brief.service;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.RiskMap;
import com.name.brief.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskMapServiceImpl implements RiskMapService {

    private final GameSessionService gameSessionService;
    private final GameRepository gameRepository;

    public RiskMapServiceImpl(GameSessionService gameSessionService,
                              GameRepository gameRepository) {
        this.gameSessionService = gameSessionService;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public void changeSectorNumber(String sectorNumber, Long gameSessionId) {
        GameSession session = gameSessionService.getSession(gameSessionId);
        Game game = session.getGame();
        if (game instanceof RiskMap) {
            ((RiskMap) game).setCurrentSectorNumber(Integer.parseInt(sectorNumber));
            gameRepository.save(game);
        }
    }
}
