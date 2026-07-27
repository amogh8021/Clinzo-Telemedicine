package com.example.telemedicine.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "availabilities",
        indexes = {
                @Index(name = "idx_availability_doctor", columnList = "doctor_id"),
                @Index(name = "idx_availability_date", columnList = "date"),
                @Index(name = "idx_availability_active", columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;


    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;


    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;


    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(
            mappedBy = "availability",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Slot> slots = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }


    public void addSlot(Slot slot) {
        slots.add(slot);
        slot.setAvailability(this);
    }


    public void removeSlot(Slot slot) {
        slots.remove(slot);
        slot.setAvailability(null);
    }
}