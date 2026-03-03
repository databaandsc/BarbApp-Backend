package com.barbapp.api.repository;

import com.barbapp.api.domain.AppointmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentItemRepository extends JpaRepository<AppointmentItem, UUID> {

    // Retrieves all services (items) that belong to a specific appointment
    List<AppointmentItem> findByAppointmentId(UUID appointmentId);
}
