package com.example.telemedicine.demo.service;

import com.example.telemedicine.demo.dto.request.DoctorRequest;
import com.example.telemedicine.demo.dto.response.DoctorResponse;
import com.example.telemedicine.demo.entity.Doctor;
import com.example.telemedicine.demo.exception.DoctorAlreadyExistsException;
import com.example.telemedicine.demo.exception.DoctorNotFoundException;
import com.example.telemedicine.demo.mapper.DoctorMapper;
import com.example.telemedicine.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;


    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {


        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email " + request.getEmail() + " already exists.");
        }

        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DoctorAlreadyExistsException("Doctor with license number " + request.getLicenseNumber() + " already exists.");
        }


        Doctor doctor = doctorMapper.toEntity(request);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toResponse(savedDoctor);
    }


    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long doctorId) {

        return doctorMapper.toResponse(findDoctor(doctorId));
    }


    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Transactional
    public DoctorResponse updateDoctor(Long doctorId, DoctorRequest request) {

        Doctor existingDoctor = findDoctor(doctorId);


        if (!existingDoctor.getEmail().equalsIgnoreCase(request.getEmail())
                && doctorRepository.existsByEmail(request.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email " + request.getEmail() + " already exists.");
        }

        if (!existingDoctor.getLicenseNumber().equalsIgnoreCase(request.getLicenseNumber())
                && doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DoctorAlreadyExistsException("Doctor with license number " + request.getLicenseNumber() + " already exists.");
        }


        existingDoctor.setName(request.getName());
        existingDoctor.setEmail(request.getEmail());
        existingDoctor.setPhoneNumber(request.getPhoneNumber());
        existingDoctor.setSpecialization(request.getSpecialization());
        existingDoctor.setLicenseNumber(request.getLicenseNumber());


        existingDoctor.setFirstVisitDuration(request.getFirstVisitDuration());
        existingDoctor.setFollowUpDuration(request.getFollowUpDuration());
        existingDoctor.setBufferTime(request.getBufferTime());
        existingDoctor.setTimezone(request.getTimezone());

        return doctorMapper.toResponse(existingDoctor);
    }


    public void deleteDoctor(Long doctorId) {

        Doctor doctor = findDoctor(doctorId);

        doctorRepository.delete(doctor);
    }


    @Transactional(readOnly = true)
    public Doctor getDoctorEntity(Long doctorId) {

        return findDoctor(doctorId);
    }


    private Doctor findDoctor(Long doctorId) {

        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
    }
}