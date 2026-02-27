package com.barbapp.api.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "barber_time_off")
public class BarberTimeOff {

    @Id
    private UUID id;

    // Relationship with the barber (Account)
    @Column(name = "barber_id", nullable = false)
    private UUID barberId;

    // Exact moment the time off begins (maps to timestamptz)
    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    // Exact moment the time off ends (maps to timestamptz)
    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    // Optional text explaining why they are away (e.g., "holidays", "sick leave")
    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // --- GETTERS & SETTERS  ---


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

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
