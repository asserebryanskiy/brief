package com.name.brief.service;

import com.name.brief.model.GameSession;
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
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

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
    public void addResponses(Player player, String responses, int roundIndex) {
        player.getDecision(roundIndex).setAnswer(responses);
        playerRepository.save(player);
    }

    @Override
    public void delete(Player player) {
        playerRepository.delete(player.getId());
    }

    @Override
    public void delete(List<Player> players) {
        playerRepository.delete(players);
    }

    @Override
    public Player addPlayer(GameSession session) {
        Player player = new Player();
        player.setGameSession(session);
        playerRepository.save(player);
        player.setUsername("player" + player.getId());
        playerRepository.save(player);
        return player;
    }
}
