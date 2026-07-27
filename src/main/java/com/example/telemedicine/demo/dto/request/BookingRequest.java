package com.example.telemedicine.demo.dto.request;

import com.example.telemedicine.demo.entity.AppointmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

    @Data
    public class BookingRequest {

        @NotNull(message = "Patient id is required")
        private Long patientId;

        @NotNull(message = "Doctor id is required")
        private Long doctorId;

        @NotNull(message = "Starting slot id is required")
        private Long slotId;

        @NotNull(message = "Appointment type is required")
        private AppointmentType appointmentType;
    }
