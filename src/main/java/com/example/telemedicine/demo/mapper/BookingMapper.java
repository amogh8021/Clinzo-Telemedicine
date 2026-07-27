package com.example.telemedicine.demo.mapper;

import com.example.telemedicine.demo.dto.response.BookingResponse;
import com.example.telemedicine.demo.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "id", target = "bookingId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    BookingResponse toResponse(Booking booking);
}