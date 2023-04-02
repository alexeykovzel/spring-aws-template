package com.alexeykovzel.example.config;

import com.alexeykovzel.example.features.account.Account;
import com.alexeykovzel.example.features.account.AccountRepository;
import com.alexeykovzel.example.features.account.Authority;
import com.alexeykovzel.example.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public void initAccounts() {
        if (!accountRepository.existsByEmail(adminEmail))
            accountRepository.save(Account.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .authorities(Authority.ADMIN.single())
                    .build());
    }
}
