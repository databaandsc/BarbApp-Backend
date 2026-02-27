package com.barbapp.api.service;

import com.barbapp.api.domain.Account;
import com.barbapp.api.domain.BarberSchedule;
import com.barbapp.api.domain.BarberTimeOff;
import com.barbapp.api.dto.CreateScheduleRequestDTO;
import com.barbapp.api.dto.CreateTimeOffRequestDTO;
import com.barbapp.api.dto.ScheduleResponseDTO;
import com.barbapp.api.dto.TimeOffResponseDTO;
import com.barbapp.api.repository.AccountRepository;
import com.barbapp.api.repository.BarberScheduleRepository;
import com.barbapp.api.repository.BarberTimeOffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final BarberScheduleRepository scheduleRepository;
    private final BarberTimeOffRepository timeOffRepository;
    private final AccountRepository accountRepository;

    // --- STANDARD WEEKLY SCHEDULE (HORARIO BASE) ---

    @Transactional
    public ScheduleResponseDTO createSchedule(CreateScheduleRequestDTO request) {
        // 1. Validate that the account exists and is indeed a Barber or Admin
        Account barber = validateBarberRole(request.barberId());

        // 2. Validate chronological order: startTime must be BEFORE endTime
        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // 3. Create the Domain Entity
        BarberSchedule schedule = new BarberSchedule();
        schedule.setId(UUID.randomUUID());
        schedule.setBarberId(barber.getId());
        schedule.setWeekday(request.weekday());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setActive(true);
        schedule.setCreatedAt(OffsetDateTime.now());
        schedule.setUpdatedAt(OffsetDateTime.now());

        // 4. Persist to DB
        BarberSchedule savedSchedule = scheduleRepository.save(schedule);

        // 5. Map back to Output DTO
        return mapToScheduleResponseDTO(savedSchedule);
    }


    // --- SPECIFIC TIME OFF (VACACIONES / AUSENCIAS) ---

    @Transactional
    public TimeOffResponseDTO createTimeOff(CreateTimeOffRequestDTO request) {
        // 1. Validate the Barber
        Account barber = validateBarberRole(request.barberId());

        // 2. Validate chronology: start date/time must be strictly before end date/time
        if (!request.startAt().isBefore(request.endAt())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // 3. Create Entity
        BarberTimeOff timeOff = new BarberTimeOff();
        timeOff.setId(UUID.randomUUID());
        timeOff.setBarberId(barber.getId());
        timeOff.setStartAt(request.startAt());
        timeOff.setEndAt(request.endAt());
        timeOff.setReason(request.reason());
        timeOff.setCreatedAt(OffsetDateTime.now());
        timeOff.setUpdatedAt(OffsetDateTime.now());

        // 4. Persist to DB
        BarberTimeOff savedTimeOff = timeOffRepository.save(timeOff);

        // 5. Map to Output DTO
        return mapToTimeOffResponseDTO(savedTimeOff);
    }


    // --- INTERNAL HELPERS (Para mantener el código limpio y profesional) ---

    private Account validateBarberRole(UUID barberId) {
        Account account = accountRepository.findById(barberId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + barberId + " no existe"));

        if (!account.getRole().name().equals("BARBER") && !account.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("El usuario seleccionado no tiene un rol válido para tener calendario");
        }
        return account;
    }

    private ScheduleResponseDTO mapToScheduleResponseDTO(BarberSchedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getId(),
                schedule.getBarberId(),
                schedule.getWeekday(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.isActive()
        );
    }

    private TimeOffResponseDTO mapToTimeOffResponseDTO(BarberTimeOff timeOff) {
        return new TimeOffResponseDTO(
                timeOff.getId(),
                timeOff.getBarberId(),
                timeOff.getStartAt(),
                timeOff.getEndAt(),
                timeOff.getReason()
        );
    }
}
