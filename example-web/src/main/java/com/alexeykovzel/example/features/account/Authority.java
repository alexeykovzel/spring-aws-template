package com.alexeykovzel.example.features.account;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public enum Authority implements GrantedAuthority {
    ADMIN,
    QUEST;

    public List<Authority> single() {
        return List.of(this);
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
