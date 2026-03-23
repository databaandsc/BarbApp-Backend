package com.barbapp.api.controller;

import com.barbapp.api.dto.BarberPublicDTO;
import com.barbapp.api.dto.ServicePublicDTO;
import com.barbapp.api.dto.AvailableSlotDTO;
import com.barbapp.api.repository.ServiceRepository;
import com.barbapp.api.service.AccountService;
import com.barbapp.api.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
// This route is configured as publicly accessible in SecurityConfig
@RequestMapping("/api/public")
public class PublicCatalogController {

    private final AccountService accountService;
    private final ServiceRepository serviceRepository;
    private final AvailabilityService availabilityService;

    /**
     * Dependency injection constructor.
     */
    public PublicCatalogController(
            AccountService accountService,
            ServiceRepository serviceRepository,
            AvailabilityService availabilityService) {
        this.accountService = accountService;
        this.serviceRepository = serviceRepository;
        this.availabilityService = availabilityService;
    }

    /**
     * Retrieves the public catalog of available barbers.
     * Does not require JWT authentication.
     */
    @GetMapping("/barbers")
    public ResponseEntity<List<BarberPublicDTO>> getCatalog() {
        return ResponseEntity.ok(accountService.findAllPublicBarbers());
    }

    /**
     * Retrieves the global list of active barbershop services.
     * Does not require JWT authentication.
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServicePublicDTO>> getBarbershopServices() {
        List<ServicePublicDTO> services = serviceRepository.findByIsActiveTrue().stream()
                .map(service -> new ServicePublicDTO(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getPrice(),
                        service.getDurationMinutes()
                ))
                .toList();

        return ResponseEntity.ok(services);
    }

    /**
     * Computes the available time slots for a specific barber on a given date.
     * Exposes the core Availability engine for unauthenticated clients.
     */
    @GetMapping("/availability")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam UUID barberId,
            @RequestParam String date) {

        LocalDate requestedDate = LocalDate.parse(date);

        // Fetch the available slots using the core availability engine
        List<AvailableSlotDTO> realSlots = availabilityService.getAvailableSlots(barberId, requestedDate);

        // Translate complex DTO object to a simple string list (e.g. "10:30") for mobile consumption
        List<String> formattedSlots = realSlots.stream()
                .map(slot -> slot.startTime().toString())
                .toList();

        return ResponseEntity.ok(formattedSlots);
    }
}
