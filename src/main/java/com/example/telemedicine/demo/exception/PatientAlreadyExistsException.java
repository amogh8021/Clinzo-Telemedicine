package com.example.telemedicine.demo.exception;

public class PatientAlreadyExistsException extends RuntimeException {

    public PatientAlreadyExistsException(String message) {
        super(message);
    }
}
