package com.example.telemedicine.demo.service;

import com.example.telemedicine.demo.dto.response.SlotResponse;
import com.example.telemedicine.demo.entity.Availability;
import com.example.telemedicine.demo.entity.Doctor;
import com.example.telemedicine.demo.entity.Slot;
import com.example.telemedicine.demo.entity.SlotStatus;
import com.example.telemedicine.demo.exception.SlotNotFoundException;
import com.example.telemedicine.demo.mapper.SlotMapper;
import com.example.telemedicine.demo.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SlotService {

    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;

    public void generateSlots(Availability availability) {

        if (slotRepository.existsByAvailability(availability)) {
            return;
        }

        Doctor doctor = availability.getDoctor();

        int slotDuration = doctor.getBaseSlotDuration();
        int buffer = doctor.getBufferTime();
        ZoneId zoneId = ZoneId.of(doctor.getTimezone());

        // 1. Start and End DateTime construct karein
        LocalDateTime currentStart = LocalDateTime.of(availability.getDate(), availability.getStartTime());
        LocalDateTime availabilityEnd = LocalDateTime.of(availability.getDate(), availability.getEndTime());

        // Agar EndTime StartTime se chhota hai (Matlab midnight 12 baje ke baad tak doctor available hai)
        if (availability.getEndTime().isBefore(availability.getStartTime())) {
            availabilityEnd = availabilityEnd.plusDays(1);
        }

        List<Slot> slots = new ArrayList<>();

        // 2. LocalDateTime to LocalDateTime comparison fix
        while (!currentStart.plusMinutes(slotDuration).isAfter(availabilityEnd)) {

            // 3. ZonedDateTime setup clean syntax
            Instant slotStart = ZonedDateTime.of(currentStart, zoneId).toInstant();
            Instant slotEnd = ZonedDateTime.of(currentStart.plusMinutes(slotDuration), zoneId).toInstant();

            Slot slot = new Slot();
            slot.setDoctor(doctor);
            slot.setAvailability(availability);
            slot.setStartTime(slotStart);
            slot.setEndTime(slotEnd);
            slot.setStatus(SlotStatus.AVAILABLE);

            slots.add(slot);

            // Time increment with duration & buffer
            currentStart = currentStart
                    .plusMinutes(slotDuration)
                    .plusMinutes(buffer);
        }

        if (!slots.isEmpty()) {
            slotRepository.saveAll(slots);
        }
    }

    @Transactional(readOnly = true)
    public List<SlotResponse> getAvailableSlots(Long doctorId) {

        return slotMapper.toResponseList(
                slotRepository.findByDoctorIdAndStatusOrderByStartTime(
                        doctorId,
                        SlotStatus.AVAILABLE
                )
        );
    }

    @Transactional(readOnly = true)
    public Slot getSlotEntity(Long slotId) {

        return slotRepository.findById(slotId)
                .orElseThrow(() ->
                        new SlotNotFoundException(
                                "Slot not found with id: " + slotId
                        ));
    }

    public void deleteAvailableSlots(Availability availability) {

        List<Slot> slots = slotRepository
                .findByAvailabilityAndStatus(
                        availability,
                        SlotStatus.AVAILABLE
                );

        slotRepository.deleteAll(slots);
    }

    public void regenerateSlots(Availability availability) {

        deleteAvailableSlots(availability);

        generateSlots(availability);
    }

    public void markBooked(Slot slot) {

        slot.setStatus(SlotStatus.BOOKED);

        slotRepository.save(slot);
    }

    public void markAvailable(Slot slot) {

        slot.setStatus(SlotStatus.AVAILABLE);

        slotRepository.save(slot);
    }
}