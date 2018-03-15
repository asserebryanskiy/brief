package com.name.brief.service;

import com.name.brief.model.Player;
import com.name.brief.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private SessionRegistry sessionRegistry;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = findByUsername(username);
        if (player == null) throw new UsernameNotFoundException("No player with username " + username);
        return player;
    }

    @Override
    public Player findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public void save(Player player) {
        playerRepository.save(player);
    }

    @Override
    public void setLoggedIn(Player player) {
        player.setLoggedIn(true);
        playerRepository.save(player);
    }

    @Override
    public void logout(Player player) {
        sessionRegistry.getAllSessions(player, false)
                .forEach(SessionInformation::expireNow);
        player.setLoggedIn(false);
        playerRepository.save(player);
    }

    @Override
    public void addResponses(Player player, String responses, int roundIndex) {
        player.getDecision(roundIndex).setAnswer(responses);
        playerRepository.save(player);
    }

    @Override
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public boolean isLoggedIn(Player player) {
        return sessionRegistry != null &&
                sessionRegistry.getAllSessions(player, false).size() > 0;
    }
}
