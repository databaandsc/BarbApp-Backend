package com.barbapp.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAppointmentRequestDTO(

        @NotNull(message = "Debes seleccionar un barbero")
        UUID barberId,

        @NotEmpty(message = "Debes seleccionar al menos un servicio para tu cita")
        List<UUID> serviceIds,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        @Future(message = "No puedes reservar una cita en el pasado")
        OffsetDateTime startAt,

        @NotNull(message = "La fecha y hora de fin es obligatoria")
        @Future(message = "No puedes reservar una cita en el pasado")
        OffsetDateTime endAt,

        String clientNotes
) {}
