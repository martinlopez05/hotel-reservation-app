package com.hotels.microservices.msvc_reservations.exception;

public class RoomNotFoundException extends RuntimeException {
  public RoomNotFoundException(String message) {
    super(message);
  }
}
