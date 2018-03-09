package com.name.brief.service;

import com.name.brief.model.Player;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface PlayerService extends UserDetailsService {
    void save(Player player);

    void login(Player player);

    void logout(Player player);

    void addResponses(Player player, String responses, int round);
}
