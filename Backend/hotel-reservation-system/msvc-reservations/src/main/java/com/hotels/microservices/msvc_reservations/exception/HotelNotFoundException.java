package com.hotels.microservices.msvc_reservations.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String message) {
        super(message);
    }
}
