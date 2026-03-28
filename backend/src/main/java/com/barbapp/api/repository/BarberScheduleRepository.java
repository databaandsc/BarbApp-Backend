package com.barbapp.api.repository;

import com.barbapp.api.domain.BarberSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BarberScheduleRepository extends JpaRepository<BarberSchedule, UUID> {

    // Retrieves all active schedules for a specific barber
    List<BarberSchedule> findByBarberIdAndIsActiveTrue(UUID barberId);

    // Retrieves the active schedule for a specific barber on a specific day of the week
    List<BarberSchedule> findByBarberIdAndWeekdayAndIsActiveTrue(UUID barberId, int weekday);
}



