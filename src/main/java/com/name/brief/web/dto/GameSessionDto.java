package com.name.brief.web.dto;

import com.name.brief.model.GameSession;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.Game;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.model.games.RiskMap;
import com.name.brief.model.games.riskmap.RiskMapType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class GameSessionDto {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final String[] gameTypes = new String[]{"Ролевая игра"};
//    private final String[] gameTypes = new String[]{"Бриф", "Карта рисков", "Ролевая игра"};
    private final String[] authenticationTypes = Arrays.stream(AuthenticationType.values())
            .map(AuthenticationType::getRussianName)
            .toArray(String[]::new);
    private String oldStrId;
    private String newStrId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activeDate = LocalDate.now();
    private String gameType = gameTypes[0];
    private Long gameSessionId;
    private String authenticationType;

    // Game specific
    private final RiskMapType[] riskMapTypes = RiskMapType.values();
    private RiskMapType riskMapType = RiskMapType.OFFICE;

    public GameSession createGameSession() {
        GameSession session = new GameSession.GameSessionBuilder(getNewStrId())
                .withActiveDate(getActiveDate())
                .withAuthenticationType(computeAuthenticationType())
                .withGame(createGame())
                .build();
        if (gameSessionId != null) session.setId(gameSessionId);
        return session;
    }

    private AuthenticationType computeAuthenticationType() {
        return Arrays.stream(AuthenticationType.values())
                .filter(type -> type.getRussianName().equals(authenticationType))
                .findAny()
                .orElse(AuthenticationType.CREATE_NEW);
    }

    private Game createGame() {
        switch (gameType) {
            case "Бриф":
                return new Brief();
            case "Карта рисков": {
                RiskMap riskMap = new RiskMap();
                riskMap.setType(riskMapType);
                return riskMap;
            }
            case "Ролевая игра":
                return new RolePlay();
        }
        return new Brief();
    }

    public String getNewStrId() {
        return newStrId == null || newStrId.isEmpty() ?
                (oldStrId == null || oldStrId.isEmpty() ?
                createGame().getEnglishName() + activeDate.format(DATE_FORMATTER) : oldStrId) : newStrId;
    }

    public static Map<String, GameSessionDto> getDtosMap(List<GameSession> sessions) {
        Map<String, GameSessionDto> dtos = new HashMap<>(sessions.size());
        sessions.forEach(s -> dtos.put("dto_" + s.getStrId(), createFrom(s)));
        return dtos;
    }

    public static GameSessionDto createFrom(GameSession session) {
        GameSessionDto dto = new GameSessionDto();
        dto.setGameSessionId(session.getId());
        dto.setOldStrId(session.getStrId());
        dto.setActiveDate(session.getActiveDate());
        dto.setGameType(session.getGame().getEnglishName());
        dto.setAuthenticationType(session.getAuthenticationType().getRussianName());
        if (session.getGame() != null) {
            Game game = session.getGame();
            if (game instanceof RiskMap) {
                dto.setRiskMapType(((RiskMap) game).getType());
            }
        }
        return dto;
    }
}
