package com.barbapp.api.dto;

import com.barbapp.api.domain.AppointmentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AppointmentResponseDTO(
        UUID id,
        UUID clientId,
        String clientName,
        String clientPhone,
        UUID barberId,
        String barberName,
        List<String> serviceNames,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        AppointmentStatus status,
        BigDecimal totalPrice,
        int totalDurationMinutes,
        String clientNotes
) {}
