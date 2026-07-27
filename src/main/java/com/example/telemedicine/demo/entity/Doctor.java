package com.example.telemedicine.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String specialization;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    // Per-Doctor Custom Appointment Durations
    @Column(name = "first_visit_duration", nullable = false)
    private Integer firstVisitDuration;

    @Column(name = "follow_up_duration", nullable = false)
    private Integer followUpDuration;

    @Column(name = "buffer_time", nullable = false)
    private Integer bufferTime = 0;

    @Column(nullable = false)
    private String timezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "doctor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Availability> availabilities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.bufferTime == null) {
            this.bufferTime = 0;
        }
    }

   
    public int getBaseSlotDuration() {
        int first = (this.firstVisitDuration != null && this.firstVisitDuration > 0) ? this.firstVisitDuration : 15;
        int follow = (this.followUpDuration != null && this.followUpDuration > 0) ? this.followUpDuration : 15;
        return gcd(first, follow);
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }
}