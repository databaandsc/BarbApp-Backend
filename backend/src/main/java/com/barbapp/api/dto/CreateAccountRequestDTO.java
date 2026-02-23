package com.barbapp.api.dto;

import com.barbapp.api.domain.Role;
import java.util.UUID;

public record CreateAccountRequestDTO(
        UUID authUserId,
        String firstName,
        String surname,
        String phone,
        Role role
) {}
