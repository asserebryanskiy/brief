package com.name.brief.service;

import com.name.brief.model.Player;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface PlayerService extends UserDetailsService {
    void save(Player player);

    void addResponses(Player player, String responses, int round);

    Player findByUsername(String username);

    void delete(Player player);

    void delete(List<Player> players);
}
