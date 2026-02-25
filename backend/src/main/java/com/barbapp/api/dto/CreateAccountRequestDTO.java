package com.barbapp.api.dto;

import com.barbapp.api.domain.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateAccountRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        String surname,
        String phone,
        Role role
) {}
