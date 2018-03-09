package com.name.brief.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public enum Role {
    PLAYER("ROLE_PLAYER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
