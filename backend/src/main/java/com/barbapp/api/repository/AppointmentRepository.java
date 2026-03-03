package com.barbapp.api.repository;

import com.barbapp.api.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Retrieves all appointments booked by a specific client
    List<Appointment> findByClientId(UUID clientId);

    // Retrieves all appointments assigned to a specific barber
    List<Appointment> findByBarberId(UUID barberId);

    // Retrieves appointments for a barber within a specific time range (e.g., a single day)
    List<Appointment> findByBarberIdAndStartAtBetween(UUID barberId, java.time.OffsetDateTime startAt, java.time.OffsetDateTime endAt);
}
