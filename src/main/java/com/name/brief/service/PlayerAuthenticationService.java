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

    /**
     * Authenticates request. Depending on authenticationType of dto it creates new player
     * in appropriate gameSession and logs it in or just logs already created player in.
     *
     * Logging in is performed by request.login("username", "password"), where
     * username is acquired by Player.constructUsername() or by taking username of already
     * existing player and password is always an empty string.
     *
     * @param dto - PLayerLoginDto containing all necessary for logging in data
     * @param request - HttpServletRequest, that will be authenticated
     */
    void authenticate(PlayerLoginDto dto, HttpServletRequest request);
}
