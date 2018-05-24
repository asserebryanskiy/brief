package com.name.brief.config.authentication;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.validation.PlayerLoginDtoValidator;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public class PlayerAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String DTO_ATTRIBUTE_NAME = "playerLoginDto";
    public static final String ERRORS_ATTRIBUTE_NAME = "org.springframework.validation.BindingResult.playerLoginDto";
    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;
    private Player player;

    @Autowired
    public PlayerAuthenticationFilter(GameSessionService gameSessionService,
                                      PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        PlayerLoginDto playerLoginDto = getDto(request);

        BindingResult result = new BeanPropertyBindingResult(playerLoginDto, "playerLoginDto");
        Validator validator = new PlayerLoginDtoValidator(gameSessionService, playerAuthenticationService);
        validator.validate(playerLoginDto, result);

        GameSession session = gameSessionService.getSession(playerLoginDto.getGameSessionStrId(), LocalDate.now());

        if (result.hasErrors()) {
            try {
                if (result.hasFieldErrors("gameSessionStrId")) {
                    response.sendRedirect("/");
                    request.getSession().setAttribute(ERRORS_ATTRIBUTE_NAME, result);
                } else {
                    response.sendRedirect("/login");
                    if (request.getHeader("referer").endsWith("/login")) {
                        request.getSession().setAttribute(ERRORS_ATTRIBUTE_NAME, result);
                    }
                    playerLoginDto.setAuthenticationType(session.getAuthenticationType());
                }
                request.getSession().setAttribute(DTO_ATTRIBUTE_NAME, playerLoginDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        player = gameSessionService.addPlayer(createPlayer(playerLoginDto), session);

        return super.attemptAuthentication(request, response);
    }

    private Player createPlayer(PlayerLoginDto dto) {
        Player player = new Player();
        player.setCommandName(dto.getCommandName());
        player.setName(dto.getName());
        player.setSurname(dto.getSurname());
        return player;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return player.getUsername();
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return player.getPassword();
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private PlayerLoginDto getDto(HttpServletRequest request) {
        PlayerLoginDto dto = new PlayerLoginDto();
        dto.setGameSessionStrId(request.getParameter("gameSessionStrId").trim().toLowerCase());
        dto.setCommandName(request.getParameter("commandName"));
        dto.setName(request.getParameter("name"));
        dto.setSurname(request.getParameter("surname"));
        return dto;
    }

}