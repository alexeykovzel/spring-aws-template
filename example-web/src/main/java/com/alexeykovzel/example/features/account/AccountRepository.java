package com.alexeykovzel.example.features.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Account.Dto findDtoByEmail(String email);

    Account findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
