package com.alexeykovzel.example.rest;

import com.alexeykovzel.example.features.account.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountsController {
    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;
    private final AuthService auth;

    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";

    @PostMapping("/register/guest")
    public void registerQuest(@RequestBody MultiValueMap<String, String> data) {
        register(data, Authority.QUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register/admin")
    public void registerAdmin(@RequestBody MultiValueMap<String, String> data) {
        register(data, Authority.ADMIN);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public List<Account> all() {
        return accountRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/all/{id}/delete")
    public void deleteById(@PathVariable String id) {
        accountRepository.deleteById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public Account.Dto account() {
        return accountRepository.findDtoByEmail(auth.getEmail());
    }

    @GetMapping("/me/roles")
    public Collection<? extends GrantedAuthority> roles() {
        return auth.getAuthorities();
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/update")
    public void update(@RequestBody MultiValueMap<String, String> data) {
        String name = getField(data, NAME_FIELD, this::verifyName);
        String email = getField(data, EMAIL_FIELD, this::verifyEmail);
        Account account = accountRepository.findByEmail(auth.getEmail());
        account.setName(name);
        account.setEmail(email);
        accountRepository.save(account);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me/delete")
    public void delete() {
        accountRepository.deleteByEmail(auth.getEmail());
        auth.logout();
    }

    private void register(MultiValueMap<String, String> data, Authority authority) {
        Credentials credentials = getCredentials(data);
        verifyCredentials(credentials);
        Account account = Account.builder()
                .name(getField(data, NAME_FIELD))
                .email(credentials.getEmail())
                .password(encoder.encode(credentials.getPassword()))
                .authorities(authority.single())
                .build();
        accountRepository.save(account);
        auth.login(credentials);
    }

    private Credentials getCredentials(MultiValueMap<String, String> data) {
        String username = getField(data, EMAIL_FIELD);
        String password = getField(data, PASSWORD_FIELD);
        return new Credentials(username, password);
    }

    private void verifyCredentials(Credentials credentials) {
        String email = credentials.getEmail();
        String password = credentials.getPassword();

        if (isEmpty(email) || isEmpty(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing e-mail or password");

        if (accountRepository.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This e-mail is already taken");

        verifyPassword(password);
        verifyEmail(email);
    }

    private void verifyName(String name) {
    }

    private void verifyEmail(String email) {
        verifyRegex(email, "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", "Invalid e-mail");
    }

    private void verifyPassword(String password) {
        verifyRegex(password, ".{4,32}", "Password should be 4-64 characters long");
        verifyRegex(password, ".*[0-9].*", "Password should contain at least one digit");
        verifyRegex(password, ".*[a-z].*", "Password should contain at least one lowercase letter");
        verifyRegex(password, ".*[A-Z].*", "Password should contain at least one uppercase letter");
    }

    private void verifyRegex(String value, String regex, String error) {
        if (!value.matches(regex)) {
            log.error("Failed to verify regex: regex='{}', error='{}'", regex, error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private String getField(MultiValueMap<String, String> data, String name, Consumer<String> verify) {
        String field = getField(data, name);
        verify.accept(field);
        return field;
    }

    private String getField(MultiValueMap<String, String> data, String name) {
        List<String> elements = data.get(name);
        return (elements == null || elements.isEmpty()) ? null : elements.get(0);
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
