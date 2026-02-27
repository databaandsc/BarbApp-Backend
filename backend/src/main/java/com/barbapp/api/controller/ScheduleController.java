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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // POST /api/schedules/base -> Create a generic weekly schedule
    @PostMapping("/base")
    public ResponseEntity<ScheduleResponseDTO> createSchedule(
            @Valid @RequestBody CreateScheduleRequestDTO request) {

        ScheduleResponseDTO createdSchedule = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    // POST /api/schedules/time-off -> Register vacations or sick leave
    @PostMapping("/time-off")
    public ResponseEntity<TimeOffResponseDTO> createTimeOff(
            @Valid @RequestBody CreateTimeOffRequestDTO request) {

        TimeOffResponseDTO createdTimeOff = scheduleService.createTimeOff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeOff);
    }
}
