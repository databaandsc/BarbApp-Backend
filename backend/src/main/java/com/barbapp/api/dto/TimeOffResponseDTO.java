package com.barbapp.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TimeOffResponseDTO(
        UUID id,
        UUID barberId,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String reason
) {}
