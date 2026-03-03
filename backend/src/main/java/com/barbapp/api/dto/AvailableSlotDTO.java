package com.barbapp.api.dto;
import java.time.LocalTime;

public record AvailableSlotDTO(
        LocalTime startTime,
        LocalTime endTime
) {}