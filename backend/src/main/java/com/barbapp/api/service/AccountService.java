package com.barbapp.api.service;

import com.barbapp.api.domain.Account;
import com.barbapp.api.dto.CreateAccountRequestDTO; // Importamos el DTO de entrada
import com.barbapp.api.dto.AccountResponseDTO;      // Importamos el DTO de salida
import com.barbapp.api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> findAccountByAuthId(UUID authUserId) {
        return accountRepository.findByAuthUserId(authUserId);
    }

    // La anotación @Transactional es mágica: si algo falla guardando a medias, deshace la operación entera para no dejar "cuentas fantasma" a medias en la BD.
    @Transactional
    public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {

        // 1. REGLA DE NEGOCIO: ¿Ya existe alguien con ese teléfono?
        if (accountRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con este número de teléfono.");
        }

        // 2. REGLA DE NEGOCIO: ¿Ese UUID de Supabase ya estaba registrado?
        if (accountRepository.findByAuthUserId(request.authUserId()).isPresent()) {
            throw new IllegalArgumentException("Esta cuenta de Supabase ya tiene un perfil asociado.");
        }

        // 3. Crear el nuevo objeto Account crudo de Base de Datos para guardarlo
        Account newAccount = new Account();
        newAccount.setId(UUID.randomUUID()); // Generamos nosotros la Clave Primaria interna
        newAccount.setAuthUserId(request.authUserId()); // El ID que nos dio Supabase
        newAccount.setFirstName(request.firstName());
        newAccount.setSurname(request.surname());
        newAccount.setPhone(request.phone());
        newAccount.setRole(request.role()); // Será CLIENT, BARBER, etc.
        newAccount.setActive(true); // Al crearla, está activa por defecto
        newAccount.setCreatedAt(OffsetDateTime.now());
        newAccount.setUpdatedAt(OffsetDateTime.now());

        // 4. Se lo damos al Encargado del Almacén para que lo guarde físicamente en Supabase
        Account savedAccount = accountRepository.save(newAccount);

        // 5. Transformamos  (Entity) a (Response DTO) para el Front
        return new AccountResponseDTO(
                savedAccount.getId(),
                savedAccount.getFirstName(),
                savedAccount.getSurname(),
                savedAccount.getPhone(),
                savedAccount.getRole(),
                savedAccount.isActive()
        );
    }
}
