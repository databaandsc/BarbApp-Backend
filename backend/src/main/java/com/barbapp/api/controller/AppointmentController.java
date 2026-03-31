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

/**
 * Appointment REST Controller / Controlador REST de Citas
 * API endpoints for managing the booking flow.
 * Endpoints de la API para gestionar el flujo de reservas.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // POST /api/appointments -> Book a new appointment / Reservar una nueva cita
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAppointmentRequestDTO request) {

        // Extract the true cryptographic identity of the user from the Supabase JWT
        // Extrae la verdadera identidad criptográfica del usuario desde el JWT de Supabase
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Pass the verified identity and the incoming request DTO to the Business Service layer
        // Pasa la identidad verificada y el DTO de la petición entrante a la capa de Servicio de Negocio
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(tokenUserId, request);

        // Return a 201 Created HTTP status along with the generated appointment details
        // Devuelve un estado HTTP 201 Created junto con los detalles de la cita generada
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    // GET /api/appointments -> List all appointments for the logged-in user / Listar todas las citas del usuario logueado
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getMyAppointments(@AuthenticationPrincipal Jwt jwt) {

        // Extrae el UUID del usuario del token
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Llama al servicio para obtener las citas del cliente
        List<AppointmentResponseDTO> myAppointments = appointmentService.getClientAppointments(tokenUserId);

        return ResponseEntity.ok(myAppointments);
    }

    // GET /api/appointments/admin/all -> List ALL appointments (Admin only) / Listar TODAS las citas (Solo Admin)
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments(@AuthenticationPrincipal Jwt jwt) {
        // Optional: could validate the ADMIN role here as extra security
        // Opcional: se podría validar el rol ADMIN aquí como seguridad adicional
        List<AppointmentResponseDTO> allAppointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(allAppointments);
    }


    // ==========================================
    // --- STATE MANAGEMENT ENDPOINTS / ENDPOINTS DE GESTIÓN DE ESTADO ---
    // ==========================================

    /**
     * Endpoint for transitioning an appointment's status (e.g., PENDING -> CONFIRMED -> COMPLETED).
     * Endpoint para transicionar el estado de una cita (ej., PENDING -> CONFIRMED -> COMPLETED).
     * 
     * Clients can only transition to CANCELLED.
     * Los clientes solo pueden transicionar a CANCELLED.
     * 
     * Barbers/Admins can transition to any valid state.
     * Los Barberos/Admins pueden transicionar a cualquier estado válido.
     */
    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID appointmentId,
            @RequestParam AppointmentStatus newStatus) {

        // Extract the authenticated user's ID from the JWT token
        // Extrae el ID del usuario autenticado desde el token JWT
        UUID tokenUserId = UUID.fromString(jwt.getSubject());

        // Delegate state transition logic and security validation to the service
        // Delega la lógica de transición de estado y la validación de seguridad al servicio
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(tokenUserId, appointmentId, newStatus);

        // Return 200 OK with the updated appointment data
        // Devuelve 200 OK con los datos de la cita actualizada
        return ResponseEntity.ok(updatedAppointment);
    }

}
