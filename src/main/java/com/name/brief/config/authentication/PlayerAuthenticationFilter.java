package com.name.brief.config.authentication;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.AuthenticationType;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public class PlayerAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    private final MessageSource messageSource;

    @Autowired
    public PlayerAuthenticationFilter(GameSessionService gameSessionService,
                                      PlayerAuthenticationService playerAuthenticationService,
                                      MessageSource messageSource) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
        this.messageSource = messageSource;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // obtain provided gameSession code
        String strId = request.getParameter("gameSessionStrId");

        // retrieve gameSession
        GameSession gameSession = gameSessionService.getSession(strId, LocalDate.now());

        // if code is not presented in DB return error
        if (gameSession == null) {
            request.getSession().setAttribute("gameSessionStrId", strId);
            request.getSession().setAttribute("flash",
                    messageSource.getMessage("player.validation.wrongGameSessionStrId", null, request.getLocale()));
            sendRedirect("/", response);
            return null;
        }

        // if session needs to collect additional information, redirect to appropriate page
        if (gameSession.authenticationType != AuthenticationType.CREATE_NEW) {
            request.getSession().setAttribute("gameSessionStrId", strId);
            request.getSession().setAttribute("authenticationType", gameSession.authenticationType);
            sendRedirect("/login", response);
            return null;
        }

        // if all information is present create and save new player if gameSession strategy demands it

        // attempt authentication of created player object
        return super.attemptAuthentication(request, response);
    }

    private void sendRedirect(String path, HttpServletResponse response) {
        try {
            response.sendRedirect(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return Player.constructUsername(request.getParameter("gameSession.strId").trim(),
                LocalDate.now(),
                request.getParameter("commandName").trim());
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return "";
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private Player getPlayer(HttpServletRequest request) {
        Player player = new Player();
        GameSession gameSession = new GameSession();
        gameSession.setStrId(request.getParameter("gameSession.strId"));
        gameSession.setActiveDate(LocalDate.now());
        player.setGameSession(gameSession);
        player.setCommandName(request.getParameter("commandName"));
        return player;
    }
}
