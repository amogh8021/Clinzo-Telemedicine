package com.example.telemedicine.demo.service;

import com.example.telemedicine.demo.dto.request.AvailabilityRequest;
import com.example.telemedicine.demo.dto.response.AvailabilityResponse;
import com.example.telemedicine.demo.entity.Availability;
import com.example.telemedicine.demo.entity.Doctor;
import com.example.telemedicine.demo.exception.AvailabilityConflictException;
import com.example.telemedicine.demo.exception.AvailabilityNotFoundException;
import com.example.telemedicine.demo.mapper.AvailabilityMapper;
import com.example.telemedicine.demo.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorService doctorService;
    private final AvailabilityMapper availabilityMapper;
    private final SlotService slotService;

    public AvailabilityResponse createAvailability(AvailabilityRequest request) {

        Doctor doctor = doctorService.getDoctorEntity(request.getDoctorId());

        // 1. Validate time window
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time."
            );
        }


        boolean overlap = availabilityRepository
                .existsByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        doctor,
                        request.getDate(),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (overlap) {
            throw new AvailabilityConflictException("Availability overlaps with an existing schedule.");
        }

        Availability availability = availabilityMapper.toEntity(request);
        availability.setDoctor(doctor);

        Availability savedAvailability = availabilityRepository.save(availability);


        slotService.generateSlots(savedAvailability);

        return availabilityMapper.toResponse(savedAvailability);
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long availabilityId) {
        return availabilityMapper.toResponse(
                findAvailability(availabilityId)
        );
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAllAvailability() {
        return availabilityRepository.findAll()
                .stream()
                .map(availabilityMapper::toResponse)
                .toList();
    }

    public void deleteAvailability(Long availabilityId) {
        Availability availability = findAvailability(availabilityId);

        // first delete the available slots
        slotService.deleteAvailableSlots(availability);

        availabilityRepository.delete(availability);
    }

    public Availability findAvailability(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() ->
                        new AvailabilityNotFoundException(
                                "Availability not found with id " + id
                        ));
    }
}