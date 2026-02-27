package com.barbapp.api.domain;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "barber_schedules")
public class BarberSchedule {

    @Id
    private UUID id;

    // Relationship with the barber (Account)
    @Column(name = "barber_id", nullable = false)
    private UUID barberId;

    // Day of the week in numeric format (e.g., 1 = Monday, 7 = Sunday)
    // Maps exactly to the 'weekday' integer column in the database
    @Column(name = "weekday", nullable = false)
    private int weekday;

    // LocalTime aligns with the 'time without time zone' PostgreSQL type
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Allows disabling a specific schedule block without hard-deleting the record
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Maps to PostgreSQL 'timestamptz'
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // --- GETTERS & SETTERS (Generados por ti) ---


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBarberId() {
        return barberId;
    }

    public void setBarberId(UUID barberId) {
        this.barberId = barberId;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
