package com.example.telemedicine.demo.repository;

import com.example.telemedicine.demo.entity.Availability;
import com.example.telemedicine.demo.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByDoctorId(Long doctorId);

    List<Availability> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    boolean existsByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Doctor doctor,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );
}
