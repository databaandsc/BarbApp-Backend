package com.barbapp.api.service;

import com.barbapp.api.domain.Account;
import com.barbapp.api.domain.Role;
import com.barbapp.api.dto.BarberPublicDTO;
import com.barbapp.api.dto.CreateAccountRequestDTO; // Importamos el DTO de entrada
import com.barbapp.api.dto.AccountResponseDTO;      // Importamos el DTO de salida
import com.barbapp.api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> findAccountByAuthId(UUID authUserId) {
        return accountRepository.findByAuthUserId(authUserId);
    }

    /**
     * Creates a new user account in the system.
     * Crea una nueva cuenta de usuario en el sistema.
     * 
     * Validates that the provided phone number and authentication ID are unique.
     * Valida que el número de teléfono y el ID de autenticación proporcionados sean únicos.
     * 
     * @param authUserId The UUID from Supabase auth. / El UUID de la autenticación de Supabase.
     * @param request The data transfer object containing the user's registration details. / El objeto de transferencia de datos con los detalles de registro.
     * @return AccountResponseDTO The created account data. / Los datos de la cuenta creada.
     * @throws IllegalArgumentException if the phone number or Auth ID already exists. / si el teléfono o Auth ID ya existen.
     */
    @Transactional
    public AccountResponseDTO createAccount(UUID authUserId,CreateAccountRequestDTO request) {

        // Validate unique phone number constraint
        if (accountRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }

        // Validate unique authentication user ID constraint
        if (accountRepository.findByAuthUserId(authUserId).isPresent()) {
            throw new IllegalArgumentException("This authentication user ID is already linked to an account.");
        }

        // Initialize and populate new Account entity
        Account newAccount = new Account();
        newAccount.setId(UUID.randomUUID());
        newAccount.setAuthUserId(authUserId);
        newAccount.setName(request.name());
        newAccount.setSurname(request.surname());
        newAccount.setPhone(request.phone());
        newAccount.setRole(request.role());
        newAccount.setActive(true);
        newAccount.setCreatedAt(OffsetDateTime.now());
        newAccount.setUpdatedAt(OffsetDateTime.now());

        // Persist the entity
        Account savedAccount = accountRepository.save(newAccount);

        // Map the persisted entity to a response DTO
        return new AccountResponseDTO(
                savedAccount.getId(),
                savedAccount.getName(),
                savedAccount.getSurname(),
                savedAccount.getPhone(),
                savedAccount.getRole(),
                savedAccount.isActive()
        );
    }

    /**
     * Returns a list of barbers for the public catalog.
     * Devuelve una lista de barberos para el catálogo público.
     * 
     * Filters accounts by Role and converts them to the secure DTO.
     * Filtra las cuentas por Rol y las convierte al DTO seguro.
     */
    public List<BarberPublicDTO> findAllPublicBarbers() {

        return accountRepository.findByRoleIn(Arrays.asList(Role.BARBER, Role.ADMIN)).stream()
                .map(account -> new BarberPublicDTO(
                        account.getId(),
                        account.getName(),
                        account.getSurname()
                ))
                .collect(Collectors.toList());
    }
}
