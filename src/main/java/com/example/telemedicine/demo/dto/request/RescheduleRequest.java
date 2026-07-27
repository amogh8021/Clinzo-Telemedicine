package com.example.telemedicine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RescheduleRequest {

    @NotNull
    private Long newSlotId;
}