package com.name.brief.service;

import com.name.brief.model.Player;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public interface PlayerAuthenticationService {
    Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId);
    void logout(Player player);
    boolean isLoggedIn(Player player);

    boolean isLoggedIn(String username);

    void setSessionRegistry(SessionRegistry registry);
}
