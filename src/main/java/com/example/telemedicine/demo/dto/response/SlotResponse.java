package com.example.telemedicine.demo.dto.response;

import com.example.telemedicine.demo.entity.SlotStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class SlotResponse {

    private Long id;

    private Long doctorId;

    private Instant startTime;

    private Instant endTime;

    private SlotStatus status;
}