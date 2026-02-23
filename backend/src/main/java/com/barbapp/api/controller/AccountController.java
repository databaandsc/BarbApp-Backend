package com.barbapp.api.controller;

import com.barbapp.api.domain.Account;
import com.barbapp.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

// 1. API REST (devuelve JSON automáticamente)
@RestController
// 2. Definimos la URL base para todos los métodos de esta clase
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    // 3. Inyección de dependencias
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 4. Endpoint GET para buscar una cuenta de Supabase por su authUserId
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<Account> getAccountByAuthId(@PathVariable UUID authUserId) {

        Optional<Account> accountOpt = accountService.findAccountByAuthId(authUserId);

        if (accountOpt.isPresent()) {
            return ResponseEntity.ok(accountOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
