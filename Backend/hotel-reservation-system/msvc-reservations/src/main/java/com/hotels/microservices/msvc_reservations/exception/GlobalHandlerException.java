package com.hotels.microservices.msvc_reservations.exception;


import com.hotels.microservices.msvc_reservations.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalHandlerException {

    //manejo de todos los errores 404
    @ExceptionHandler({
            ReservationNotFoundException.class,
            RoomNotFoundException.class,
            HotelNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundExceptions(RuntimeException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(RoomIsReservedException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoomIsReservedException(RoomIsReservedException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExternalServiceException(ExternalServiceException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("Data invalid: " + e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

}
