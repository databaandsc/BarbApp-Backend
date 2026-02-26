package com.barbapp.api.controller;

import com.barbapp.api.dto.AppointmentResponseDTO;
import com.barbapp.api.dto.CreateAppointmentRequestDTO;
import com.barbapp.api.service.AppointmentService;
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
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // POST /api/appointments -> Book a new appointment
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAppointmentRequestDTO request) {

        // Extract the true cryptographic identity of the user from the Supabase JWT
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Pass the verified identity and the incoming request DTO to the Business Service layer
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(tokenUserId, request);

        // Return a modern 201 Created HTTP status along with the generated appointment details
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }
}
