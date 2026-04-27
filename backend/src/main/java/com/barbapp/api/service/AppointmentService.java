package com.barbapp.api.service;

import com.barbapp.api.domain.*;
import com.barbapp.api.dto.AppointmentResponseDTO;
import com.barbapp.api.dto.CreateAppointmentRequestDTO;
import com.barbapp.api.repository.AccountRepository;
import com.barbapp.api.repository.AppointmentItemRepository;
import com.barbapp.api.repository.AppointmentRepository;
import com.barbapp.api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentItemRepository appointmentItemRepository;
    private final AccountRepository accountRepository;
    private final ServiceRepository serviceRepository;

    // @Transactional ensures that if any part of this process fails (e.g., saving items fails),
    // the entire transaction is rolled back and no partial data is saved to the database.
    // @Transactional asegura que si cualquier parte del proceso falla, la transacción entera 
    // se revierte y no se guardan datos parciales en la base de datos.
    @Transactional
    public AppointmentResponseDTO createAppointment(UUID clientAuthId, CreateAppointmentRequestDTO request) {

        // 1. Validate that the client exists using the ID extracted from the JWT
        // 1. Validar que el cliente exista usando el ID extraído del JWT
        Account client = accountRepository.findByAuthUserId(clientAuthId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado en el sistema"));

        // 2. Validate that the selected barber exists and has the correct role
        // 2. Validar que el barbero seleccionado exista y tenga el rol correcto
        Account barber = accountRepository.findById(request.barberId())
                .orElseThrow(() -> new IllegalArgumentException("El barbero seleccionado no existe"));

        if (!barber.getRole().name().equals("BARBER") && !barber.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("El usuario seleccionado no es un barbero válido");
        }

        // 3. Initialize the base Appointment (without final price or duration yet)
        // 3. Inicializar la Cita base (sin precio final ni duración todavía)
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setClientId(client.getId());
        appointment.setBarberId(barber.getId());
        appointment.setStartAt(request.startAt());
        appointment.setEndAt(request.endAt());
        appointment.setStatus(AppointmentStatus.PENDING); // Security: force PENDING status on creation
        appointment.setClientNotes(request.clientNotes());
        appointment.setCreatedAt(OffsetDateTime.now());
        appointment.setUpdatedAt(OffsetDateTime.now());

        // 4. Process the 'Shopping Cart' of requested services
        // 4. Procesar el 'Carrito de la Compra' de servicios solicitados
        BigDecimal calculatedTotalPrice = BigDecimal.ZERO;
        int calculatedTotalDuration = 0;
        List<AppointmentItem> itemsToSave = new ArrayList<>();

        for (UUID serviceId : request.serviceIds()) {
            // Verify that the service exists in the catalog
            // Verificar que el servicio exista en el catálogo
            com.barbapp.api.domain.Service catalogService = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("El servicio con ID " + serviceId + " no existe"));

            // Verify that the service is still active
            // Verificar que el servicio siga estando activo
            if (!catalogService.isActive()) {
                throw new IllegalArgumentException("El servicio '" + catalogService.getName() + "' ya no está disponible");
            }

            // Create the line item for the appointment (Historical Snapshot)
            // Crear la línea de artículo para la cita (Captura Histórica)
            AppointmentItem item = new AppointmentItem();
            item.setId(UUID.randomUUID());
            item.setAppointmentId(appointment.getId());
            item.setServiceId(catalogService.getId());
            item.setQuantity(1); // Default to 1 person per service for now
            item.setPriceSnapshot(catalogService.getPrice());
            item.setDurationSnapshotMinutes(catalogService.getDurationMinutes());

            // Accumulate totals
            // Acumular totales
            calculatedTotalPrice = calculatedTotalPrice.add(catalogService.getPrice());
            calculatedTotalDuration += catalogService.getDurationMinutes();

            itemsToSave.add(item);
        }

        // 5. Assign the calculated totals back to the main Appointment and save to the database
        // 5. Asignar los totales calculados de vuelta a la Cita principal y guardar en la base de datos
        appointment.setTotalPrice(calculatedTotalPrice);
        appointment.setTotalDurationMinutes(calculatedTotalDuration);

        // Save everything inside the transaction
        // Guardar todo dentro de la transacción
        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentItemRepository.saveAll(itemsToSave);

        // 6. Return the clean DTO to the client
        // 6. Devolver el DTO limpio al cliente
        return mapToResponseDTO(savedAppointment);
    }

    /**
     * Retrieves all appointments for the authenticated client.
     * Obtiene todas las citas para el cliente autenticado.
     */
    public List<AppointmentResponseDTO> getClientAppointments(UUID clientAuthId) {

        Account client = accountRepository.findByAuthUserId(clientAuthId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado en el sistema"));

        List<Appointment> appointments = appointmentRepository.findByClientId(client.getId());

        return appointments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Helper method to map domain entities to a clean response DTO.
     * Método auxiliar para mapear entidades del dominio a un DTO de respuesta limpio.
     */
    private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {

        // 1. Resolve client details (Name and Phone)
        // 1. Resolver detalles del cliente (Nombre y Teléfono)
        String clientName = accountRepository.findById(appointment.getClientId())
                .map(account -> account.getName() + " " + account.getSurname())
                .orElse("Cliente Desconocido");

        String clientPhone = accountRepository.findById(appointment.getClientId())
                .map(Account::getPhone)
                .orElse("Sin Teléfono");

        // 2. Resolve barber details
        // 2. Resolver detalles del barbero
        String barberName = accountRepository.findById(appointment.getBarberId())
                .map(account -> account.getName() + " " + account.getSurname())
                .orElse("Barbero Desconocido");

        // 3. Fetch requested services for this appointment and extract their names
        // 3. Obtener los servicios solicitados para esta cita y extraer sus nombres
        List<String> serviceNames = appointmentItemRepository.findByAppointmentId(appointment.getId())
                .stream()
                .map(item -> serviceRepository.findById(item.getServiceId())
                        .map(com.barbapp.api.domain.Service::getName)
                        .orElse("Servicio Borrado"))
                .toList();

        // 4. Return the enriched DTO
        // 4. Devolver el DTO enriquecido
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getClientId(),
                clientName,
                clientPhone,
                appointment.getBarberId(),
                barberName,
                serviceNames,
                appointment.getStartAt(),
                appointment.getEndAt(),
                appointment.getStatus(),
                appointment.getTotalPrice(),
                appointment.getTotalDurationMinutes(),
                appointment.getClientNotes()
        );
    }


    /**
     * Returns ALL appointments in the system (Admin only).
     * Devuelve TODAS las citas en el sistema (Solo Administrador).
     */
    public List<AppointmentResponseDTO> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ==========================================
    // --- STATE MANAGEMENT / GESTIÓN DE ESTADO ---
    // ==========================================

    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(UUID tokenUserId, UUID appointmentId, AppointmentStatus newStatus) {

        // 1. Fetch the target appointment
        // 1. Obtener la cita objetivo
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("La cita no existe."));

        // 2. Fetch the requesting user's account to evaluate permissions
        // 2. Obtener la cuenta del usuario solicitante para evaluar permisos
        Account requestor = accountRepository.findByAuthUserId(tokenUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado."));

        // 3. SECURITY & AUTHORIZATION RULES
        // 3. REGLAS DE SEGURIDAD Y AUTORIZACIÓN
        boolean isAdmin = requestor.getRole().name().equals("ADMIN");
        boolean isTheAssignedBarber = requestor.getId().equals(appointment.getBarberId());
        boolean isTheClient = requestor.getId().equals(appointment.getClientId());

        if (newStatus == AppointmentStatus.CANCELLED) {
            // Clients, Assigned Barbers, and Admins can all CANCEL an appointment
            // Los Clientes, Barberos Asignados y Administradores pueden CANCELAR una cita
            if (!isAdmin && !isTheAssignedBarber && !isTheClient) {
                throw new SecurityException("No tienes permiso para cancelar esta cita.");
            }
        } else {
            // Only Assigned Barbers and Admins can transition to CONFIRMED, COMPLETED, NO_SHOW, etc.
            // Solo los Barberos Asignados y Administradores pueden transicionar a CONFIRMED, COMPLETED, NO_SHOW, etc.
            if (!isAdmin && !isTheAssignedBarber) {
                throw new SecurityException("No tienes permiso para gestionar esta cita.");
            }
        }

        // 4. Update the status and audit trail
        // 4. Actualizar el estado y rastro de auditoría
        appointment.setStatus(newStatus);
        appointment.setDecidedBy(requestor.getId());
        appointment.setDecidedAt(OffsetDateTime.now());
        appointment.setUpdatedAt(OffsetDateTime.now());

        // 5. Persist to database & return the mapped DTO
        // 5. Persistir en la base de datos y devolver el DTO mapeado
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDTO(savedAppointment);
    }

}
