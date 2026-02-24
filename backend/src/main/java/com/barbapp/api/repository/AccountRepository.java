package com.barbapp.api.repository;

import com.barbapp.api.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // Spring Data JPA generará la consulta SQL automáticamente por el nombre del método
    Optional<Account> findByAuthUserId(UUID authUserId);

    Optional<Account> findByPhone(String phone);
}
