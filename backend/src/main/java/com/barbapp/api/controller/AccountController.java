package com.barbapp.api.controller;

import com.barbapp.api.domain.Account;
import com.barbapp.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barbapp.api.dto.CreateAccountRequestDTO;
import com.barbapp.api.dto.AccountResponseDTO;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Retrieves an account representation by its associated authentication user ID.
     * 
     * @param authUserId The UUID provided by the authentication provider.
     * @return ResponseEntity containing the Account entity if found, or 404 Not Found.
     */
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<Account> getAccountByAuthId(@PathVariable UUID authUserId) {

        Optional<Account> accountOpt = accountService.findAccountByAuthId(authUserId);

        if (accountOpt.isPresent()) {
            return ResponseEntity.ok(accountOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Registers a new user account in the system.
     * 
     * @param request The data transfer object containing the user's registration details.
     * @return ResponseEntity with the created AccountResponseDTO and HTTP Status 201 Created.
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody CreateAccountRequestDTO request) {

        // Delegate business logic and persistence to the service layer
        AccountResponseDTO createdAccount = accountService.createAccount(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
}

