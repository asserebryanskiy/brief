package com.name.web.dto;

import com.name.games.GameType;
import com.name.model.GameSession;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class GameSessionDto {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String strId;
    private String activeDateStr = LocalDate.now().format(DATE_FORMATTER);
    private GameType gameType = GameType.BRIEF;
    private int numberOfCommands = 5;

    public GameSession createGameSession() {
        return new GameSession.GameSessionBuilder(getStrId())
                .withActiveDate(getActiveDate())
                .withNumberOfCommands(numberOfCommands)
                .withGameType(gameType)
                .build();
    }

    public LocalDate getActiveDate() {
        return LocalDate.parse(activeDateStr, DATE_FORMATTER);
    }

    public String getStrId() {
        return strId == null || strId.isEmpty() ? gameType.name().toLowerCase() + activeDateStr : strId;
    }
}
