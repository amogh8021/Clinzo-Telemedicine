package com.example.telemedicine.demo.exception;

public class AvailabilityConflictException extends RuntimeException{
    public AvailabilityConflictException(String message){
        super(message);
    }
}
