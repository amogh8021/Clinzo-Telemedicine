package com.example.telemedicine.demo.exception;

public class AvailabilityNotFoundException extends RuntimeException{
    public AvailabilityNotFoundException(String message){
        super(message);
    }
}
