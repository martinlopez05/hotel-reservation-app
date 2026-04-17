package com.hotels.microservices.msvc_reservations.exception;

public class ExternalServiceException extends RuntimeException {
  public ExternalServiceException(String message) {
    super(message);
  }
}
