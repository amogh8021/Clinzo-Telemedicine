package com.example.telemedicine.demo.exception;

public class SlotAlreadyBookedException extends RuntimeException {
    public SlotAlreadyBookedException(String message){
        super(message);
    }
}
