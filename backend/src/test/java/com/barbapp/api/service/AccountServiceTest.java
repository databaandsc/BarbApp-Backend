package com.barbapp.api.service;

import com.barbapp.api.domain.Account;
import com.barbapp.api.domain.Role;
import com.barbapp.api.dto.BarberPublicDTO;
import com.barbapp.api.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// ExtendWith enables Mockito annotations like @Mock and @InjectMocks.
// ExtendWith habilita las anotaciones de Mockito como @Mock y @InjectMocks.
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    // 1. Create a "fake" repository (Mock). It will NOT touch the real PostgreSQL database.
    // 1. Creamos un repositorio "falso" (Mock). NO tocará la base de datos PostgreSQL real.
    @Mock
    private AccountRepository accountRepository;

    // 2. Inject the fake repository inside the real AccountService.
    // 2. Inyectamos el repositorio falso dentro del AccountService real.
    @InjectMocks
    private AccountService accountService;

    @Test
    public void testFindAllPublicBarbers_returnsFilteredBarbers() {
        // Create completely fake Barber and Admin accounts.
        // Creamos cuentas de Barbero y Administrador completamente falsas.
        Account barber = new Account();
        barber.setId(UUID.randomUUID());
        barber.setName("John");
        barber.setRole(Role.BARBER);

        Account admin = new Account();
        admin.setId(UUID.randomUUID());
        admin.setName("Jane");
        admin.setRole(Role.ADMIN);

        // We instruct the mock: "When findByRoleIn is asked for BARBER and ADMIN, return these fake objects".
        // Le instruimos al mock: "Cuando se te pida findByRoleIn buscando BARBER y ADMIN, devuelve estos objetos falsos".
        when(accountRepository.findByRoleIn(Arrays.asList(Role.BARBER, Role.ADMIN)))
                .thenReturn(Arrays.asList(barber, admin));

        // --- ACT (Ejecución) ---
        // Call the real method we are testing.
        // Llamamos al método real que estamos testeando.
        List<BarberPublicDTO> result = accountService.findAllPublicBarbers();

        // --- ASSERT (Comprobación) ---
        // Verify it mapped the results perfectly to DTOs without breaking.
        // Verificamos que se mapearon los resultados a DTOs perfectamente sin romperse.
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).name());
        assertEquals("Jane", result.get(1).name());
    }
}
