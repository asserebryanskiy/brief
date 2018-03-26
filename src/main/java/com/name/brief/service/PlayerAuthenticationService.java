package com.name.brief.service;

import com.name.brief.model.Player;

import java.util.Set;

public interface PlayerAuthenticationService {
    Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId);
    void logout(Player player);
    boolean isLoggedIn(Player player);
}
