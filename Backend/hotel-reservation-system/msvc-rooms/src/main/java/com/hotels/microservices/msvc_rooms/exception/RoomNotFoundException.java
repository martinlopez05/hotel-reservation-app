package com.hotels.microservices.msvc_rooms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomNotFoundException extends RuntimeException {
    private String code;

    public RoomNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }
}
