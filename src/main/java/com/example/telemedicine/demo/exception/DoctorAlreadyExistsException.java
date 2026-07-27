package com.example.telemedicine.demo.exception;

public class DoctorAlreadyExistsException extends RuntimeException{
    public DoctorAlreadyExistsException(String message){
        super(message);
    }
}
