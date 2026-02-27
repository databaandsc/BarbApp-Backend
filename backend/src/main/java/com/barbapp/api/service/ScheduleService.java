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

    // --- STANDARD WEEKLY SCHEDULE ---

    @Transactional
    public ScheduleResponseDTO createSchedule(UUID tokenUserId, CreateScheduleRequestDTO request) {
        // 1. Validate permissions and target barber existence
        Account barber = validateBarberAndPermissions(request.barberId(), tokenUserId);

        // 2. Validate chronological order: startTime must be strictly before endTime
        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // 3. Initialize the Domain Entity
        BarberSchedule schedule = new BarberSchedule();
        schedule.setId(UUID.randomUUID());
        schedule.setBarberId(barber.getId());
        schedule.setWeekday(request.weekday());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setActive(true);
        schedule.setCreatedAt(OffsetDateTime.now());
        schedule.setUpdatedAt(OffsetDateTime.now());

        // 4. Persist to database
        BarberSchedule savedSchedule = scheduleRepository.save(schedule);

        // 5. Map to response DTO
        return mapToScheduleResponseDTO(savedSchedule);
    }


    // --- SPECIFIC TIME OFF ---

    @Transactional
    public TimeOffResponseDTO createTimeOff(UUID tokenUserId, CreateTimeOffRequestDTO request) {
        // 1. Validate permissions and target barber existence
        Account barber = validateBarberAndPermissions(request.barberId(), tokenUserId);

        // 2. Validate chronology: start timestamp must be strictly before end timestamp
        if (!request.startAt().isBefore(request.endAt())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // 3. Initialize the Domain Entity
        BarberTimeOff timeOff = new BarberTimeOff();
        timeOff.setId(UUID.randomUUID());
        timeOff.setBarberId(barber.getId());
        timeOff.setStartAt(request.startAt());
        timeOff.setEndAt(request.endAt());
        timeOff.setReason(request.reason());
        timeOff.setCreatedAt(OffsetDateTime.now());
        timeOff.setUpdatedAt(OffsetDateTime.now());

        // 4. Persist to database
        BarberTimeOff savedTimeOff = timeOffRepository.save(timeOff);

        // 5. Map to response DTO
        return mapToTimeOffResponseDTO(savedTimeOff);
    }


    // --- INTERNAL HELPERS ---

    /**
     * Validates that the requesting user has authorization to modify the target barber's schedule.
     * Only ADMINs can modify any schedule. BARBERs can only modify their own schedule.
     */
    private Account validateBarberAndPermissions(UUID targetBarberId, UUID tokenUserId) {

        // Retrieve the requesting user's account
        Account requestor = accountRepository.findByAuthUserId(tokenUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        // Retrieve the target barber's account
        Account targetBarber = accountRepository.findById(targetBarberId)
                .orElseThrow(() -> new IllegalArgumentException("El barbero seleccionado no existe"));

        // Check authorization rules
        boolean isAdmin = requestor.getRole().name().equals("ADMIN");
        boolean isSelf = requestor.getId().equals(targetBarber.getId());

        if (!isAdmin && !isSelf) {
            throw new SecurityException("No tienes permisos para modificar el calendario de este usuario");
        }

        // Verify the target account is actually a barber or admin
        if (!targetBarber.getRole().name().equals("BARBER") && !targetBarber.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("El usuario seleccionado no tiene un rol válido para tener calendario");
        }

        return targetBarber;
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
