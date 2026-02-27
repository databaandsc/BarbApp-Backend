package com.barbapp.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateTimeOffRequestDTO(

        @NotNull(message = "El ID del barbero es obligatorio")
        UUID barberId,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        @Future(message = "Las ausencias no pueden crearse en el pasado")
        OffsetDateTime startAt,

        @NotNull(message = "La fecha y hora de fin es obligatoria")
        @Future(message = "Las ausencias no pueden crearse en el pasado")
        OffsetDateTime endAt,

        String reason
) {}
