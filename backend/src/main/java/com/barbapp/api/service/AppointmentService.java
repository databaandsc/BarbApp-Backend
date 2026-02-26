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
    @Transactional
    public AppointmentResponseDTO createAppointment(UUID clientAuthId, CreateAppointmentRequestDTO request) {

        // 1. Validate that the client exists using the ID extracted from the JWT
        Account client = accountRepository.findByAuthUserId(clientAuthId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado en el sistema"));

        // 2. Validate that the selected barber exists and has the correct role
        Account barber = accountRepository.findById(request.barberId())
                .orElseThrow(() -> new IllegalArgumentException("El barbero seleccionado no existe"));

        if (!barber.getRole().name().equals("BARBER") && !barber.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("El usuario seleccionado no es un barbero válido");
        }

        // 3. Initialize the base Appointment (without final price or duration yet)
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
        BigDecimal calculatedTotalPrice = BigDecimal.ZERO;
        int calculatedTotalDuration = 0;
        List<AppointmentItem> itemsToSave = new ArrayList<>();

        for (UUID serviceId : request.serviceIds()) {
            // Verify that the service exists in the catalog
            com.barbapp.api.domain.Service catalogService = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("El servicio con ID " + serviceId + " no existe"));

            // Verify that the service is still active
            if (!catalogService.isActive()) {
                throw new IllegalArgumentException("El servicio '" + catalogService.getName() + "' ya no está disponible");
            }

            // Create the line item for the appointment (Historical Snapshot)
            AppointmentItem item = new AppointmentItem();
            item.setId(UUID.randomUUID());
            item.setAppointmentId(appointment.getId());
            item.setServiceId(catalogService.getId());
            item.setQuantity(1); // Default to 1 person per service for now
            item.setPriceSnapshot(catalogService.getPrice());
            item.setDurationSnapshotMinutes(catalogService.getDurationMinutes());

            // Accumulate totals
            calculatedTotalPrice = calculatedTotalPrice.add(catalogService.getPrice());
            calculatedTotalDuration += catalogService.getDurationMinutes();

            itemsToSave.add(item);
        }

        // 5. Assign the calculated totals back to the main Appointment and save to the database
        appointment.setTotalPrice(calculatedTotalPrice);
        appointment.setTotalDurationMinutes(calculatedTotalDuration);

        // Save everything inside the transaction
        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentItemRepository.saveAll(itemsToSave);

        // 6. Return the clean DTO to the client
        return mapToResponseDTO(savedAppointment);
    }

    // Retrieves all appointments for the authenticated client
    public List<AppointmentResponseDTO> getClientAppointments(UUID clientAuthId) {

        Account client = accountRepository.findByAuthUserId(clientAuthId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado en el sistema"));

        List<Appointment> appointments = appointmentRepository.findByClientId(client.getId());

        return appointments.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Helper method to keep the main logic clean
    private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getClientId(),
                appointment.getBarberId(),
                appointment.getStartAt(),
                appointment.getEndAt(),
                appointment.getStatus(),
                appointment.getTotalPrice(),
                appointment.getTotalDurationMinutes(),
                appointment.getClientNotes()
        );
    }
}
