package com.barbapp.api.dto;

import java.util.UUID;

public record PublicBarberDTO(
        UUID id,
        String firstName,
        String lastName,
        String profileImageUrl // Optional
) {}
