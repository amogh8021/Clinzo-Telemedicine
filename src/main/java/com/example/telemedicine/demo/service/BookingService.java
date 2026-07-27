package com.example.telemedicine.demo.service;

import com.example.telemedicine.demo.dto.request.BookingRequest;
import com.example.telemedicine.demo.dto.response.BookingResponse;
import com.example.telemedicine.demo.entity.*;
import com.example.telemedicine.demo.exception.BadRequestException;
import com.example.telemedicine.demo.exception.BookingNotFoundException;
import com.example.telemedicine.demo.exception.ResourceNotFoundException;
import com.example.telemedicine.demo.exception.SlotUnavailableException;
import com.example.telemedicine.demo.mapper.BookingMapper;
import com.example.telemedicine.demo.repository.BookingRepository;
import com.example.telemedicine.demo.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final BookingMapper bookingMapper;

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final SlotService slotService;

    public BookingResponse createBooking(BookingRequest request) {

        Patient patient = patientService.getPatientEntity(request.getPatientId());
        Doctor doctor = doctorService.getDoctorEntity(request.getDoctorId());
        Slot startingSlot = slotService.getSlotEntity(request.getSlotId());

        validateSlotOwnershipAndAvailability(startingSlot, doctor);

        int appointmentDuration = (request.getAppointmentType() == AppointmentType.FIRST_VISIT)
                ? doctor.getFirstVisitDuration()
                : doctor.getFollowUpDuration();

        int baseSlotDuration = doctor.getBaseSlotDuration();
        int requiredSlotsCount = (appointmentDuration / baseSlotDuration);

        List<Slot> contiguousSlots = findRequiredSlots(startingSlot, requiredSlotsCount);

        try {
            reserveSlots(contiguousSlots);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new SlotUnavailableException("The slot was booked by another patient.");
        }

        Booking booking = new Booking();
        booking.setPatient(patient);
        booking.setDoctor(doctor);
        booking.setSlots(contiguousSlots);
        booking.setAppointmentType(request.getAppointmentType());
        booking.setStartTime(contiguousSlots.getFirst().getStartTime());
        booking.setEndTime(contiguousSlots.getLast().getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse rescheduleBooking(Long bookingId, BookingRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id : " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cancelled booking cannot be rescheduled.");
        }

        Doctor doctor = doctorService.getDoctorEntity(request.getDoctorId());
        Slot newStartingSlot = slotService.getSlotEntity(request.getSlotId());

        validateSlotOwnershipAndAvailability(newStartingSlot, doctor);

        int appointmentDuration = (request.getAppointmentType() == AppointmentType.FIRST_VISIT)
                ? doctor.getFirstVisitDuration()
                : doctor.getFollowUpDuration();

        int baseSlotDuration = doctor.getBaseSlotDuration();
        int requiredSlotsCount = appointmentDuration / baseSlotDuration;

              List<Slot> newSlots = findRequiredSlots(newStartingSlot, requiredSlotsCount);

        try {
            reserveSlots(newSlots);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new SlotUnavailableException("Selected starting slot or consecutive block is no longer available.");
        }


        List<Slot> oldSlots = new ArrayList<>(booking.getSlots());
        releaseSlots(oldSlots);

        booking.setDoctor(doctor);
        booking.setAppointmentType(request.getAppointmentType());
        booking.setSlots(newSlots);
        booking.setStartTime(newSlots.getFirst().getStartTime());
        booking.setEndTime(newSlots.getLast().getEndTime());

        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(updatedBooking);
    }

    private void validateSlotOwnershipAndAvailability(Slot slot, Doctor doctor) {
        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("Selected slot does not belong to the specified doctor.");
        }

        if (!Boolean.TRUE.equals(slot.getAvailability().getActive())) {
            throw new BadRequestException("Availability is inactive.");
        }
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking not found with id : " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled.");
        }

        booking.cancel();
        releaseSlots(booking.getSlots());

        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(updatedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking not found with id : " + bookingId));

        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByDoctor(Long doctorId) {

        doctorService.getDoctorEntity(doctorId);

        return bookingRepository.findByDoctorId(doctorId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByPatient(Long patientId) {

        patientService.getPatientEntity(patientId);

        return bookingRepository.findByPatientId(patientId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }


    private List<Slot> findRequiredSlots(Slot startingSlot, int requiredCount) {
        if (startingSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotUnavailableException("The selected starting slot is no longer available.");
        }

        if (requiredCount == 1) {
            return List.of(startingSlot);
        }

        List<Slot> upcomingSlots = slotRepository
                .findByAvailabilityIdAndStartTimeGreaterThanEqualOrderByStartTimeAsc(
                        startingSlot.getAvailability().getId(),
                        startingSlot.getStartTime()
                );

        if (upcomingSlots.isEmpty() || !upcomingSlots.getFirst().getId().equals(startingSlot.getId())) {
            throw new SlotUnavailableException("Invalid slot sequence.");
        }

        List<Slot> selectedSlots = new ArrayList<>();
        selectedSlots.add(startingSlot);

        long bufferMinutes = startingSlot.getDoctor().getBufferTime();

        for (int i = 1; i < requiredCount; i++) {
            Slot previousSlot = selectedSlots.get(i - 1);


            Instant expectedNextStart = previousSlot.getEndTime().plus(Duration.ofMinutes(bufferMinutes));


            Slot nextSlot = upcomingSlots.stream()
                    .filter(s -> s.getStartTime().equals(expectedNextStart))
                    .findFirst()
                    .orElseThrow(() -> new SlotUnavailableException("Required contiguous time block is unavailable."));

            if (nextSlot.getStatus() != SlotStatus.AVAILABLE) {
                throw new SlotUnavailableException("Consecutive time block is unavailable for this appointment duration.");
            }

            selectedSlots.add(nextSlot);
        }

        return selectedSlots;
    }

    private void reserveSlots(List<Slot> slots) {
        for (Slot slot : slots) {
            slot.setStatus(SlotStatus.BOOKED);
        }
        slotRepository.saveAll(slots);
    }

    private void releaseSlots(List<Slot> slots) {
        for (Slot slot : slots) {
            slot.setStatus(SlotStatus.AVAILABLE);
        }
        slotRepository.saveAll(slots);
    }
}