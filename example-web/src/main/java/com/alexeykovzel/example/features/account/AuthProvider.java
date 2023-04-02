package com.alexeykovzel.example.features.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthProvider implements AuthenticationProvider {
    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Verify that credentials are valid
        Account account = accountRepository.findByEmail(email);
        if (account == null || !encoder.matches(password, account.getPassword())) {
            log.error("Wrong credentials: email='{}', password='{}'", email, password);
            throw new BadCredentialsException("Wrong username or password");
        }

        // Apply user authorities for this session
        log.info("Authorizing user: email='{}', authorities={}", email, account.getAuthorities().toString());
        return new UsernamePasswordAuthenticationToken(email, password, account.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
