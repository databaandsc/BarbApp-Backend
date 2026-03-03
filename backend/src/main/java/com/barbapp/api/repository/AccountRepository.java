package com.barbapp.api.repository;

import com.barbapp.api.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAuthUserId(UUID authUserId);

    Optional<Account> findByPhone(String phone);

    // Retrieves a list of accounts based on their role (e.g., to find all Barbers)
    List<Account> findByRoleIn(List<com.barbapp.api.domain.Role> roles);

}
