package com.barbapp.api.dto;

import java.util.UUID;

/**
 * DTO for the public view of the barber catalog.
 * It only contains basic, non-sensitive information that anyone can view without logging in.
 */
public record BarberPublicDTO(
        UUID id,
        String name,
        String surname
) {}