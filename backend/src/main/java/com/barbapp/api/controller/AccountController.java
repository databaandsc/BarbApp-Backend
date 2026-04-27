package com.barbapp.api.controller;

import com.barbapp.api.domain.Account;
import com.barbapp.api.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barbapp.api.dto.CreateAccountRequestDTO;
import com.barbapp.api.dto.AccountResponseDTO;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Retrieves an account representation by its associated authentication user ID.
     * Recupera una representación de la cuenta a través de su ID de usuario de autenticación asociado.
     * 
     * @param authUserId The UUID provided by the authentication provider. / El UUID proporcionado por el proveedor de autenticación.
     * @return ResponseEntity containing the Account entity if found, or 404 Not Found. / ResponseEntity que contiene la entidad de Cuenta si se encuentra, o 404 No Encontrado.
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
     * Registra una nueva cuenta de usuario en el sistema.
     * 
     * @param request The data transfer object containing the user's registration details. / El objeto de transferencia de datos con los detalles de registro del usuario.
     * @return ResponseEntity with the created AccountResponseDTO and HTTP Status 201 Created. / ResponseEntity con el AccountResponseDTO creado y el Estado HTTP 201 Creado.
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAccountRequestDTO request) {

        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        AccountResponseDTO createdAccount = accountService.createAccount(tokenUserId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

}

