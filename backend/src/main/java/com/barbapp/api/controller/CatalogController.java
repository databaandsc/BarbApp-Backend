package com.barbapp.api.controller;

import com.barbapp.api.dto.PublicBarberDTO;
import com.barbapp.api.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    /**
     * Public endpoint to fetch the list of all active barbers and admins.
     *
     * URL: GET /api/public/catalog/barbers
     *
     * @return 200 OK with a JSON array of PublicBarberDTO
     */
    @GetMapping("/barbers")
    public ResponseEntity<List<PublicBarberDTO>> getBarbers() {

        List<PublicBarberDTO> barbers = catalogService.getActiveBarbers();

        return ResponseEntity.ok(barbers);
    }
}
