package com.example.telemedicine.demo.mapper;

import com.example.telemedicine.demo.dto.request.AvailabilityRequest;
import com.example.telemedicine.demo.dto.response.AvailabilityResponse;
import com.example.telemedicine.demo.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slots", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Availability toEntity(AvailabilityRequest request);

    @Mapping(source = "doctor.id", target = "doctorId")
    AvailabilityResponse toResponse(Availability availability);
}