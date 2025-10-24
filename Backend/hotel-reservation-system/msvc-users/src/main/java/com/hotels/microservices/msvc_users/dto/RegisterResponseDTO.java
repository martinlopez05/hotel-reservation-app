package com.hotels.microservices.msvc_users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDTO {
    private String message;
}
