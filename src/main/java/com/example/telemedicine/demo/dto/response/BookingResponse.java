package com.example.telemedicine.demo.dto.response;

import com.example.telemedicine.demo.entity.AppointmentType;
import com.example.telemedicine.demo.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
@Data
@Builder
public class BookingResponse {

    private Long bookingId;

    private Long patientId;

    private Long doctorId;

    private AppointmentType appointmentType;

    private BookingStatus status;

    private Instant startTime;

    private Instant endTime;

    private Instant bookedAt;
}