package com.example.telemedicine.demo.controller;

import com.example.telemedicine.demo.dto.request.AvailabilityRequest;
import com.example.telemedicine.demo.dto.response.AvailabilityResponse;
import com.example.telemedicine.demo.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;


    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @Valid @RequestBody AvailabilityRequest request) {

        AvailabilityResponse response = availabilityService.createAvailability(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{availabilityId}")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable Long availabilityId) {

        return ResponseEntity.ok(
                availabilityService.getAvailability(availabilityId)
        );
    }


    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAllAvailabilities() {

        return ResponseEntity.ok(
                availabilityService.getAllAvailability()
        );
    }


    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long availabilityId) {

        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }
}