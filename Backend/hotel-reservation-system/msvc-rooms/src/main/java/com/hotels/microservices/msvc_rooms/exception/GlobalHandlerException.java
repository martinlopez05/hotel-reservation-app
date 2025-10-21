package com.hotels.microservices.msvc_rooms.exception;

import com.hotels.microservices.msvc_rooms.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorDTO> HandlerRoomNotFoundException(RoomNotFoundException ex, HttpServletRequest request){
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getCode())
                .path(request.getRequestURI())
                .detail("Room Not Found")
                .build();
        return  new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .errorCode("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .detail(errors.toString())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

}
