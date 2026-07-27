package com.example.telemedicine.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorRequest {


    @NotBlank(message = "Doctor name is required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "First visit duration is required")
    @Min(value = 5, message = "First visit duration must be at least 5 minutes")
    @Max(value = 180,message = "First visit duration must be at max 180 minutes" )
    private Integer firstVisitDuration;

    @NotNull(message = "Follow-up duration is required")
    @Min(value = 5, message = "Follow-up duration must be at least 5 minutes")
    @Max(value = 180,message = "Follow-up duration must be at max 180 minutes" )
    private Integer followUpDuration;

    @NotBlank(message = "License number is required")
    private String licenseNumber;


    @Min(value = 0, message = "Buffer time cannot be negative")
    @Max(value = 60 ,message = "buffer time should not be more than 1 hr")
    private Integer bufferTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;
}