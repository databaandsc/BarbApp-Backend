package com.barbapp.api.dto;
import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponseDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int durationMinutes
) { }