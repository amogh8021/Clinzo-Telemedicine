package com.example.telemedicine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AvailabilityResponse {

    private Long id;

    private Long doctorId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean active;

    private Instant createdAt;
}