package com.barbapp.api.controller;

import com.barbapp.api.dto.CreateServiceRequestDTO;
import com.barbapp.api.dto.ServiceResponseDTO;
import com.barbapp.api.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    /**
     * GET /api/services
     * Returns a list of all active barbershop services.
     */
    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> services = serviceCatalogService.getAllActiveServices();
        return ResponseEntity.ok(services); // Returns HTTP 200 OK
    }

    /**
     * POST /api/services
     * Creates a new barbershop service.
     */
    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(@Valid @RequestBody CreateServiceRequestDTO request) {
        ServiceResponseDTO createdService = serviceCatalogService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService); // Returns HTTP 201 Created
    }
}
