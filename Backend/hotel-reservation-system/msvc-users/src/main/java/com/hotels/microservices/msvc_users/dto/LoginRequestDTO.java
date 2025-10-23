package com.hotels.microservices.msvc_users.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String email;
    private String password;


}
