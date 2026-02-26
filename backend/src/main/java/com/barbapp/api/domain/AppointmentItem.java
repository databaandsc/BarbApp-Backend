package com.barbapp.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "appointment_services")
public class AppointmentItem {

    @Id
    private UUID id;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "price_snapshot", nullable = false, precision = 5, scale = 2)
    private BigDecimal priceSnapshot;

    @Column(name = "duration_snapshot_minutes", nullable = false)
    private int durationSnapshotMinutes;

    // --- GETTERS & SETTERS  ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public int getDurationSnapshotMinutes() {
        return durationSnapshotMinutes;
    }

    public void setDurationSnapshotMinutes(int durationSnapshotMinutes) {
        this.durationSnapshotMinutes = durationSnapshotMinutes;
    }
}
