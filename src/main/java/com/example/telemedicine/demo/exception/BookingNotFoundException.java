package com.example.telemedicine.demo.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message){
        super(message);
    }

}
