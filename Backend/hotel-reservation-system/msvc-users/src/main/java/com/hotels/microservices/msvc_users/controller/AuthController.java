package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.dto.*;
import com.hotels.microservices.msvc_users.service.AuthService;
import com.hotels.microservices.msvc_users.service.IServiceUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    AuthService authService;


    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        return authService.register(registerRequestDTO);
    }


    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO authRequestDTO){
        return authService.login(authRequestDTO);
    }

}

