package com.name.brief.service;

import com.name.brief.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayerAuthenticationServiceImpl implements PlayerAuthenticationService {
    private SessionRegistry playerSessionRegistry;

    @Override
    public Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId) {
        return playerSessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof Player &&
                        ((Player) p).getGameSession().getId().equals(gameSessionId))
                .map(p -> ((Player) p).getUsername())
                .collect(Collectors.toSet());
    }

    @Override
    public void logout(Player player) {
        playerSessionRegistry.getAllSessions(player, false)
                .forEach(SessionInformation::expireNow);
    }

    @Override
    public boolean isLoggedIn(Player player) {
        return playerSessionRegistry.getAllSessions(player, false).size() > 0;
    }

    @Override
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.playerSessionRegistry = sessionRegistry;
    }
}
