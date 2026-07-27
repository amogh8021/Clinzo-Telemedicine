package com.example.telemedicine.demo.mapper;

import com.example.telemedicine.demo.dto.request.DoctorRequest;
import com.example.telemedicine.demo.dto.response.DoctorResponse;
import com.example.telemedicine.demo.entity.Doctor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    Doctor toEntity(DoctorRequest request);

    DoctorResponse toResponse(Doctor doctor);
}