package com.barbapp.api.service;

import com.barbapp.api.domain.Account;
import com.barbapp.api.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    // Inyección de dependencias por constructor
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Método de negocio
    public Optional<Account> findAccountByAuthId(UUID authUserId) {
        return accountRepository.findByAuthUserId(authUserId);
    }
}
