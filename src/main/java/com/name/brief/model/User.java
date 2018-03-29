package com.name.brief.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@ToString(exclude = "gameSessions")
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    @NotNull
    private String username;
    @NotNull
    private String password;
    private String role;
    @OneToMany(mappedBy = "user")
    private List<GameSession> gameSessions;

    public User() {
        super();
    }

    public User(String username, String password, String role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        gameSessions = new ArrayList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
