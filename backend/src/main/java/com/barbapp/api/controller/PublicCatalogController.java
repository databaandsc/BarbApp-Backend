package com.barbapp.api.controller;

import com.barbapp.api.dto.BarberPublicDTO;
import com.barbapp.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// This is the route configurated as public in SecurityConfig
@RequestMapping("/api/public")
public class PublicCatalogController {

    private final AccountService accountService;

    // Inject the service
    public PublicCatalogController(AccountService accountService) {
        this.accountService = accountService;
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
}