package com.name.brief.utils;

import com.name.brief.model.games.Conference;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameUtils {
    private static final Logger logger = LoggerFactory.getLogger(GameUtils.class);

    public static int getPhaseIndexByName(Game game, String phaseName) {
        return game.getPhases().stream()
                .filter(p -> p.getEnglishName().equals(phaseName))
                .map(Phase::getOrderIndex)
                .findAny()
                .orElseThrow(() -> {
                    logger.error("Phase with name {} was not found", phaseName);
                    return new IllegalArgumentException();
                });
    }
}
