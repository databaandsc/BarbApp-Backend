package com.barbapp.api.controller;

import com.barbapp.api.dto.AvailableSlotDTO;
import com.barbapp.api.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Public endpoint to fetch availability slots.
 * Accessible without authentication as it's under /api/public/**
 */
@RestController
@RequestMapping("/api/public/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    /**
     * Endpoint to fetch all available time slots for a specific barber on a given date.
     * URL Example: GET /api/public/availability?barberId=...&date=2026-08-20
     */
    @GetMapping
    public ResponseEntity<List<AvailableSlotDTO>> getBarberAvailability(
            @RequestParam UUID barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Delegate the complex mathematical computation of free slots to the Service Layer
        List<AvailableSlotDTO> availableSlots = availabilityService.getAvailableSlots(barberId, date);

        // Return the final list to the client with an HTTP 200 OK status
        return ResponseEntity.ok(availableSlots);
    }
}
