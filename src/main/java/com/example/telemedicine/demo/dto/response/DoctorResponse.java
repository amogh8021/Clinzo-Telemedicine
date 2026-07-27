package com.example.telemedicine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DoctorResponse {

    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private String specialization;

    private Integer consultationDuration;

    private Integer bufferTime;

    private String timezone;

    private Instant createdAt;
}