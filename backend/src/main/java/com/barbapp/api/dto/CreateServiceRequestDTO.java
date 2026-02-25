package com.barbapp.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateServiceRequestDTO(

        @NotBlank(message = "El nombre del servicio es obligatorio")
        String name,
        String description,
        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe ser al menos de 1 minuto")
        Integer durationMinutes,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        BigDecimal price
) {}
