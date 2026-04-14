package com.hotels.microservices.msvc_reservations.exception;

public class RoomIsReservedException extends RuntimeException {
    public RoomIsReservedException(String message) {
        super(message);
    }
}
