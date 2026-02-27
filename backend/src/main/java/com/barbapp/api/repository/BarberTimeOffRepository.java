package com.barbapp.api.repository;

import com.barbapp.api.domain.BarberTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BarberTimeOffRepository extends JpaRepository<BarberTimeOff, UUID> {

    // Retrieves all upcoming time off entries for a specific barber
    List<BarberTimeOff> findByBarberIdAndEndAtAfter(UUID barberId, OffsetDateTime now);

}
