package com.barbapp.api.dto;

import java.time.LocalTime;
import java.util.UUID;

public record ScheduleResponseDTO(
        UUID id,
        UUID barberId,
        int weekday,
        LocalTime startTime,
        LocalTime endTime,
        boolean isActive
) {}
