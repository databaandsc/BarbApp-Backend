package com.barbapp.api.service;

import com.barbapp.api.domain.Appointment;
import com.barbapp.api.domain.BarberSchedule;
import com.barbapp.api.domain.BarberTimeOff;
import com.barbapp.api.dto.AvailableSlotDTO;
import com.barbapp.api.repository.AccountRepository;
import com.barbapp.api.repository.AppointmentRepository;
import com.barbapp.api.repository.BarberScheduleRepository;
import com.barbapp.api.repository.BarberTimeOffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final BarberScheduleRepository scheduleRepository;
    private final BarberTimeOffRepository timeOffRepository;
    private final AppointmentRepository appointmentRepository;
    private final AccountRepository accountRepository;

    private static final int SLOT_DURATION_MINUTES = 30;

    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(UUID barberId, LocalDate date) {

        // 1. Verify existence and role
        accountRepository.findById(barberId)
                .filter(acc -> acc.getRole().name().equals("BARBER") || acc.getRole().name().equals("ADMIN"))
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un barbero válido con ese ID"));

        // 2. Discover the day of the week
        int weekday = date.getDayOfWeek().getValue();

        // 3. Obtain the Base Schedule
        BarberSchedule schedule = scheduleRepository.findByBarberIdAndWeekdayAndIsActiveTrue(barberId, weekday)
                .orElse(null);

        // If they don't work that day, return empty list
        if (schedule == null) {
            return new ArrayList<>();
        }

        // 4. Transform LocalDate to OffsetDateTime boundary markers
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = date.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        // 5. Fetch overlapping Time Offs (Ausencias específicas por horas)
        List<BarberTimeOff> timeOffs = timeOffRepository.findByBarberIdAndStartAtBeforeAndEndAtAfter(barberId, endOfDay, startOfDay);

        // 6. Fetch existing appointments
        List<Appointment> existingAppointments = appointmentRepository.findByBarberIdAndStartAtBetween(barberId, startOfDay, endOfDay);

        // 7. GENERATE THE SLOTS
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        LocalTime currentSlotStart = schedule.getStartTime();

        while (currentSlotStart.plusMinutes(SLOT_DURATION_MINUTES).isBefore(schedule.getEndTime()) ||
                currentSlotStart.plusMinutes(SLOT_DURATION_MINUTES).equals(schedule.getEndTime())) {

            LocalTime currentSlotEnd = currentSlotStart.plusMinutes(SLOT_DURATION_MINUTES);
            boolean isSlotTaken = false;

            // 7.1 Check against actual appointments
            for (Appointment appointment : existingAppointments) {
                LocalTime appointmentStart = appointment.getStartAt().toLocalTime();
                LocalTime appointmentEnd = appointment.getEndAt().toLocalTime();

                if (currentSlotStart.isBefore(appointmentEnd) && currentSlotEnd.isAfter(appointmentStart)) {
                    isSlotTaken = true;
                    break; // Early exit: slot is already dead, stop checking more appointments
                }
            }

            // 7.2 Check against Time Offs (Only if it survived the previous check)
            if (!isSlotTaken) {
                for (BarberTimeOff timeOff : timeOffs) {
                    LocalTime timeOffStart = timeOff.getStartAt().toLocalTime();
                    LocalTime timeOffEnd = timeOff.getEndAt().toLocalTime();

                    if (currentSlotStart.isBefore(timeOffEnd) && currentSlotEnd.isAfter(timeOffStart)) {
                        isSlotTaken = true;
                        break; // Early exit: slot conflicts with time off, stop checking other absences
                    }
                }
            }

            // 7.3 If free from both appointments and time-offs, add to list
            if (!isSlotTaken) {
                availableSlots.add(new AvailableSlotDTO(currentSlotStart, currentSlotEnd));
            }

            // Advance the cursor for the next iteration (e.g., from 10:00 to 10:30)
            currentSlotStart = currentSlotStart.plusMinutes(SLOT_DURATION_MINUTES);
        }

        return availableSlots;
    }
}
