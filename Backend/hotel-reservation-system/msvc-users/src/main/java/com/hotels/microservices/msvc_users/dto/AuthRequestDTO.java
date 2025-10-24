package com.hotels.microservices.msvc_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank
    @NotNull(message = "Username is required")
    private String username;

    @NotBlank
    @NotNull(message = "Password is required")
    private String password;

}
