package com.barbapp.api.controller;

import com.barbapp.api.dto.CreateScheduleRequestDTO;
import com.barbapp.api.dto.CreateTimeOffRequestDTO;
import com.barbapp.api.dto.ScheduleResponseDTO;
import com.barbapp.api.dto.TimeOffResponseDTO;
import com.barbapp.api.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // POST /api/schedules/base -> Create a generic weekly schedule
    @PostMapping("/base")
    public ResponseEntity<ScheduleResponseDTO> createSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateScheduleRequestDTO request) {

        // Extract the authenticated user's ID from the JWT Subject
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Delegate schedule creation and authorization validation to the Service layer
        ScheduleResponseDTO createdSchedule = scheduleService.createSchedule(tokenUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    // POST /api/schedules/time-off -> Register vacations or specific time off
    @PostMapping("/time-off")
    public ResponseEntity<TimeOffResponseDTO> createTimeOff(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTimeOffRequestDTO request) {

        // Extract the authenticated user's ID from the JWT Subject
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Delegate time-off creation and authorization validation to the Service layer
        TimeOffResponseDTO createdTimeOff = scheduleService.createTimeOff(tokenUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeOff);
    }
}
