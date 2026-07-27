package com.example.telemedicine.demo.service;

import com.example.telemedicine.demo.dto.request.PatientRequest;
import com.example.telemedicine.demo.dto.response.PatientResponse;
import com.example.telemedicine.demo.entity.Patient;
import com.example.telemedicine.demo.exception.PatientAlreadyExistsException;
import com.example.telemedicine.demo.exception.PatientNotFoundException;
import com.example.telemedicine.demo.mapper.PatientMapper;
import com.example.telemedicine.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;


    public PatientResponse createPatient(PatientRequest request) {

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new PatientAlreadyExistsException(
                    "Patient with email " + request.getEmail() + " already exists."
            );
        }

        Patient patient = patientMapper.toEntity(request);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(savedPatient);
    }


    @Transactional(readOnly = true)
    public Patient getPatientEntity(Long id) {
        return findPatient(id);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        return patientMapper.toResponse(findPatient(id));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }


    public PatientResponse updatePatient(Long id, PatientRequest request) {

        Patient existingPatient = findPatient(id);

        if (!existingPatient.getEmail().equalsIgnoreCase(request.getEmail())
                && patientRepository.existsByEmail(request.getEmail())) {

            throw new PatientAlreadyExistsException(
                    "Patient with email " + request.getEmail() + " already exists."
            );
        }

        existingPatient.setName(request.getName());
        existingPatient.setEmail(request.getEmail());
        existingPatient.setPhoneNumber(request.getPhoneNumber());

        return patientMapper.toResponse(existingPatient);
    }


    public void deletePatient(Long id) {

        Patient patient = findPatient(id);

        patientRepository.delete(patient);
    }

   //helper method for findthepatient by id
    private Patient findPatient(Long id) {

        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(
                                "Patient not found with id : " + id
                        ));
    }
}