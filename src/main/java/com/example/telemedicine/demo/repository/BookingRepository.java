package com.example.telemedicine.demo.repository;

import com.example.telemedicine.demo.entity.Booking;
import com.example.telemedicine.demo.entity.BookingStatus;
import com.example.telemedicine.demo.entity.Slot;
import com.example.telemedicine.demo.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByDoctorId(Long doctorId);

    List<Booking> findByPatientId(Long patientId);

    List<Booking> findByStatus(BookingStatus status);

    boolean existsByPatientIdAndStatus(
            Long patientId,
            BookingStatus status
    );
    List<Slot> findByDoctorIdOrderByStartTime(Long doctorId);

    List<Booking> findByDoctorIdAndStatus(
            Long doctorId,
            BookingStatus status
    );



}