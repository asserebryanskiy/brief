package com.name.brief.web.dto;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.RiskMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class GameSessionDto {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String[] gameTypes = new String[]{"Бриф", "Карта рисков"};
    private String strId;
    private String activeDateStr = LocalDate.now().format(DATE_FORMATTER);
    private String gameType = gameTypes[0];
    private int numberOfCommands = 5;

    public GameSession createGameSession() {
        return new GameSession.GameSessionBuilder(getStrId())
                .withActiveDate(getActiveDate())
                .withNumberOfCommands(numberOfCommands)
                .withGame(createGame())
                .build();
    }

    private Game createGame() {
        switch (gameType) {
            case "Бриф":
                return new Brief();
            case "Карта рисков":
                return new RiskMap();
        }
        return new Brief();
    }

    public LocalDate getActiveDate() {
        return LocalDate.parse(activeDateStr, DATE_FORMATTER);
    }

    public String getStrId() {
        return strId == null || strId.isEmpty() ? createGame().getEnglishName() + activeDateStr : strId;
    }
}
