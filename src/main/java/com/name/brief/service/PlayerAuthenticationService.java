package com.name.brief.service;

import com.name.brief.model.Player;
import org.springframework.security.core.session.SessionRegistry;

import java.util.Set;

public interface PlayerAuthenticationService {
    Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId);
    void logout(Player player);
    boolean isLoggedIn(Player player);

    void setSessionRegistry(SessionRegistry sessionRegistry);
}
