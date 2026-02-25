package com.barbapp.api.service;

import com.barbapp.api.domain.Service;
import com.barbapp.api.dto.CreateServiceRequestDTO;
import com.barbapp.api.dto.ServiceResponseDTO;
import com.barbapp.api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    /**
     * Gets all active barbershop services for the client app.
     */
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getAllActiveServices() {
        return serviceRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new barbershop service (Admin only).
     */
    @Transactional
    public ServiceResponseDTO createService(CreateServiceRequestDTO request) {
        Service service = new Service();
        service.setId(UUID.randomUUID());
        service.setName(request.name());
        service.setDescription(request.description());
        service.setDurationMinutes(request.durationMinutes());
        service.setPrice(request.price());
        service.setActive(true);
        service.setCreatedAt(OffsetDateTime.now());
        service.setUpdatedAt(OffsetDateTime.now());

        Service savedService = serviceRepository.save(service);
        return mapToResponseDTO(savedService);
    }

    /**
     * Helper method to map Entity -> DTO
     */
    private ServiceResponseDTO mapToResponseDTO(Service service) {
        return new ServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.isActive()
        );
    }
}
