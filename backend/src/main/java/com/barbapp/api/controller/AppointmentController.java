package com.barbapp.api.controller;

import com.barbapp.api.domain.AppointmentStatus;
import com.barbapp.api.dto.AppointmentResponseDTO;
import com.barbapp.api.dto.CreateAppointmentRequestDTO;
import com.barbapp.api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        // Return a 201 Created HTTP status along with the generated appointment details
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    // GET /api/appointments -> List all appointments for the logged-in user
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(@AuthenticationPrincipal Jwt jwt) {

        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        List<AppointmentResponseDTO> myAppointments = appointmentService.getClientAppointments(tokenUserId);

        return ResponseEntity.ok(myAppointments);
    }

    // GET /api/appointments/admin/all -> List ALL appointments (Admin only)
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments(@AuthenticationPrincipal Jwt jwt) {
        // Optional: could validate the ADMIN role here as extra security
        List<AppointmentResponseDTO> allAppointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(allAppointments);
    }


    // ==========================================
    // --- STATE MANAGEMENT ENDPOINTS ---
    // ==========================================

    /**
     * Endpoint for transitioning an appointment's status (e.g., PENDING -> CONFIRMED -> COMPLETED).
     * Clients can only transition to CANCELLED.
     * Barbers/Admins can transition to any valid state.
     */
    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID appointmentId,
            @RequestParam AppointmentStatus newStatus) {

        // Extract the authenticated user's ID from the JWT token
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Delegate state transition logic and security validation to the service
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(tokenUserId, appointmentId, newStatus);

        // Return 200 OK with the updated appointment data
        return ResponseEntity.ok(updatedAppointment);
    }

}
