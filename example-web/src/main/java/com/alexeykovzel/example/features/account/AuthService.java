package com.alexeykovzel.example.features.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthProvider provider;

    public String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public void login(Credentials credentials) throws ResponseStatusException {
        try {
            var token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
            SecurityContextHolder.getContext().setAuthentication(provider.authenticate(token));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
