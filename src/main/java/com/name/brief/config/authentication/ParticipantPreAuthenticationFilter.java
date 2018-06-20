package com.name.brief.config.authentication;

import com.name.brief.config.ParticipantSecurityConfig;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.time.LocalDate;

public class ParticipantPreAuthenticationFilter extends GenericFilterBean {

    public static final String CODE_ERROR_ATTR_NAME = "codeError";

    private final RequestMatcher shouldFilterRequestMatcher;
    private final GameSessionService gameSessionService;
    private final PlayerService playerService;

    public ParticipantPreAuthenticationFilter(GameSessionService gameSessionService,
                                              PlayerService playerService) {
        this.gameSessionService = gameSessionService;
        this.playerService = playerService;
        this.shouldFilterRequestMatcher = new AntPathRequestMatcher(
                ParticipantSecurityConfig.LOGIN_PROCESSING_URL, "POST");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest &&
                shouldFilterRequestMatcher.matches((HttpServletRequest) request)) {
            // retrieve gameSession code
            String code = request.getParameter("gameSessionStrId").trim().toLowerCase();

            // check it
            GameSession session = gameSessionService.getSession(code, LocalDate.now());
            if (session != null) {
                // add participant
                Player player = playerService.addPlayer(session);

                // add username and password parameter to the request
                ParticipantLoginRequest updatedReq =
                        new ParticipantLoginRequest((HttpServletRequest) request, player);

                chain.doFilter(updatedReq, response);
            } else {
                // add validation error to response
                ((HttpServletRequest) request).getSession().setAttribute(CODE_ERROR_ATTR_NAME, true);

                // doFilter
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public static class ParticipantLoginRequest extends HttpServletRequestWrapper {
        private final Player player;

        ParticipantLoginRequest(HttpServletRequest request, Player player) {
            super(request);
            this.player = player;
        }

        @Override
        public String getParameter(String name) {
            switch (name) {
                case "username": return player.getUsername();
                case "password": return player.getPassword();
                default: return super.getParameter(name);
            }
        }

    }
}
