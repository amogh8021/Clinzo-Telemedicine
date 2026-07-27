package com.example.telemedicine.demo.mapper;

import com.example.telemedicine.demo.dto.response.SlotResponse;
import com.example.telemedicine.demo.entity.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SlotMapper {

    @Mapping(source = "doctor.id", target = "doctorId")
    SlotResponse toResponse(Slot slot);
    List<SlotResponse> toResponseList(List<Slot> slots);
}