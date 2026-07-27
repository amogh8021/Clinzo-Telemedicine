package com.example.telemedicine.demo.controller;

import com.example.telemedicine.demo.dto.request.PatientRequest;
import com.example.telemedicine.demo.dto.response.PatientResponse;
import com.example.telemedicine.demo.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Register a new patient.
     */
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response = patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get all patients.
     */
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * Update an existing patient.
     */
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientRequest request) {

        return ResponseEntity.ok(
                patientService.updatePatient(patientId, request)
        );
    }

    /**
     * Delete a patient.
     */
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long patientId) {

        patientService.deletePatient(patientId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientById(patientId)
        );
    }
}