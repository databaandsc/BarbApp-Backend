package com.barbapp.api.controller;

import com.barbapp.api.dto.BarberPublicDTO;
import com.barbapp.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.barbapp.api.repository.ServiceRepository;
import com.barbapp.api.dto.ServicePublicDTO;

import java.util.List;

@RestController
// This is the route configurated as public in SecurityConfig
@RequestMapping("/api/public")
public class PublicCatalogController {

    private final AccountService accountService;
    private final ServiceRepository serviceRepository;

    // Inject the service
    public PublicCatalogController(AccountService accountService, ServiceRepository serviceRepository) {
        this.accountService = accountService;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Public endpoint to list all barbers in the catalog.
     * Does not require a JWT token.
     */
    @GetMapping("/barbers")
    public ResponseEntity<List<BarberPublicDTO>> getCatalog() {
        List<BarberPublicDTO> barbers = accountService.findAllPublicBarbers();
        return ResponseEntity.ok(accountService.findAllPublicBarbers());
    }

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
}