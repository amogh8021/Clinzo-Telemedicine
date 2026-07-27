package com.example.telemedicine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PatientResponse {

    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private Instant createdAt;
}