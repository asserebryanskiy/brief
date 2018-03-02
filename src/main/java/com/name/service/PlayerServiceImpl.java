package com.name.service;

import com.name.model.Player;
import com.name.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    private Player findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public void save(Player player) {
        playerRepository.save(player);
    }

    @Override
    public void login(Player player) {
        player.setLoggedIn(true);
        playerRepository.save(player);
    }

    @Override
    public void logout(Player player) {
        player.setLoggedIn(false);
        playerRepository.save(player);
    }
}
