package com.example.telemedicine.demo.controller;

import com.example.telemedicine.demo.dto.response.SlotResponse;
import com.example.telemedicine.demo.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                slotService.getAvailableSlots(doctorId)
        );
    }
}