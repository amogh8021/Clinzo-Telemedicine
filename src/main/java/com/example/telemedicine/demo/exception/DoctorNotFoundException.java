package com.example.telemedicine.demo.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message){
        super(message);
    }
}
