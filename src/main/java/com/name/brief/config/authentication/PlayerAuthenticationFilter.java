package com.name.brief.config.authentication;

import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.validation.PlayerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public class PlayerAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public PlayerAuthenticationFilter(GameSessionService gameSessionService,
                                      PlayerAuthenticationService playerAuthenticationService) {
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Player player = getPlayer(request);

        BindingResult result = new BeanPropertyBindingResult(player, "player");
        Validator validator = new PlayerValidator(gameSessionService, playerAuthenticationService);
        validator.validate(player, result);

        if (result.hasErrors()) {
            request.getSession().setAttribute("player", player);
            request.getSession().setAttribute("org.springframework.validation.BindingResult.player", result);
            throw new BadCredentialsException("Invalid gameSession id or command name");
        }
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return Player.constructUsername(request.getParameter("gameSession.strId"),
                LocalDate.now(),
                request.getParameter("commandName"));
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
