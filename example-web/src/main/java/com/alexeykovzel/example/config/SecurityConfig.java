package com.alexeykovzel.example.config;

import com.alexeykovzel.example.features.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.csrf().disable();

        http.authorizeRequests(requests -> requests
                .antMatchers(HttpMethod.GET, new String[]{}).authenticated()
                .antMatchers(HttpMethod.GET, new String[]{}).hasAnyAuthority("ADMIN")
                .anyRequest().permitAll());

        // The user is redirected to login page if request requires any authority
        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/accounts/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler((request, response, error) -> response.sendError(403, error.getMessage()))
                .successHandler((request, response, auth) -> response.setStatus(200))
                .permitAll());

        // Clear session details on log out
        http.logout(logout -> logout
                .logoutUrl("/accounts/me/logout")
                .logoutSuccessHandler(((request, response, authentication) -> {}))
                .deleteCookies("JSESSIONID"));

        // Remember session details for 1 hour
        http.rememberMe(rememberMe -> rememberMe
                .userDetailsService(accountService)
                .tokenValiditySeconds(3600)
                .key("uniqueAndSecret"));

        // Handle http to https redirection
        http.requiresChannel(channel -> channel
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}