package com.name.service;

import com.name.model.Player;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface PlayerService extends UserDetailsService {
    void save(Player player);

    void login(Player player);

    void logout(Player player);
}
