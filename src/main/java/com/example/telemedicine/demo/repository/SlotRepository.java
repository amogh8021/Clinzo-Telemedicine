package com.example.telemedicine.demo.repository;

import com.example.telemedicine.demo.entity.Availability;
import com.example.telemedicine.demo.entity.Slot;
import com.example.telemedicine.demo.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByAvailabilityIdAndStartTimeGreaterThanEqualOrderByStartTimeAsc(
            Long availabilityId,
            Instant startTime
    );

    List<Slot> findByAvailabilityAndStatus(
            Availability availability,
            SlotStatus status
    );

    List<Slot> findByDoctorIdAndStatusOrderByStartTime(
            Long doctorId,
            SlotStatus status
    );

    boolean existsByAvailability(Availability availability);
}
