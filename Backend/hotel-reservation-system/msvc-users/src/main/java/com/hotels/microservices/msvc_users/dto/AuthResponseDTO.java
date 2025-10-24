package com.hotels.microservices.msvc_users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

    private String username;
    private String email;
    private String role;
    private String token;
}
