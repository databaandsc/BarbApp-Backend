package com.barbapp.api.service;

import com.barbapp.api.domain.Role;
import com.barbapp.api.dto.PublicBarberDTO;
import com.barbapp.api.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final AccountRepository accountRepository;

    /**
     * Retrieves all active barbers and admins to display them in the public catalog.
     *
     * @return List of PublicBarberDTO containing basic, safe information.
     */
    @Transactional(readOnly = true)
    public List<PublicBarberDTO> getActiveBarbers() {

        // Define which roles are considered "schedulable" staff
        List<Role> staffRoles = Arrays.asList(Role.BARBER, Role.ADMIN);

        // Fetch them from the database
        return accountRepository.findByRoleIn(staffRoles)
                .stream()
                .map(account -> new PublicBarberDTO(
                        account.getId(),
                        account.getName(),
                        account.getSurname(),
                        null // Placeholder for future profile image URL implementation
                ))
                .collect(Collectors.toList());
    }
}

