package com.hotels.microservices.msvc_rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String detail;
    private String path;


}
