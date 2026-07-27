package com.example.telemedicine.demo.mapper;

import com.example.telemedicine.demo.dto.request.PatientRequest;
import com.example.telemedicine.demo.dto.response.PatientResponse;
import com.example.telemedicine.demo.entity.Patient;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequest patientRequest);
    PatientResponse toResponse(Patient patient);

}
