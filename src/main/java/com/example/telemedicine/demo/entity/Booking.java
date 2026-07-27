package com.example.telemedicine.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_booking_patient", columnList = "patient_id"),
                @Index(name = "idx_booking_doctor", columnList = "doctor_id"),
                @Index(name = "idx_booking_status", columnList = "status"),
                @Index(name = "idx_booking_start", columnList = "start_time")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToMany
    @JoinTable(
            name = "booking_slots",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "slot_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_booking_slot",
                    columnNames = {"slot_id"}
            )
    )
    private List<Slot> slots = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", length = 30)
    private AppointmentType appointmentType;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    // Flag for Retroactive availability modifications
    @Column(name = "needs_reschedule", nullable = false)
    private Boolean needsReschedule = false;

    @Column(name = "booked_at", nullable = false, updatable = false)
    private Instant bookedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.bookedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }
}