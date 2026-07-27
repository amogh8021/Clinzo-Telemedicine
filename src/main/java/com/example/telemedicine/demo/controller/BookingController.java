package com.example.telemedicine.demo.controller;

import com.example.telemedicine.demo.dto.request.BookingRequest;
import com.example.telemedicine.demo.dto.response.BookingResponse;
import com.example.telemedicine.demo.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId)
        );
    }


    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByDoctor(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByDoctor(doctorId)
        );
    }


    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByPatient(patientId)
        );
    }


    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId)
        );
    }

    @PatchMapping("/{bookingId}/reschedule")
    public ResponseEntity<BookingResponse> rescheduleBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(
                bookingService.rescheduleBooking(bookingId, request)
        );
    }
    }
