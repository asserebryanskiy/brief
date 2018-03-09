package com.name.brief.service;

import com.name.brief.model.User;
import com.name.brief.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User found = userRepository.findByUsername(username);
        if (found == null) throw new UsernameNotFoundException("Haven't found user with username " + username);
        return found;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
