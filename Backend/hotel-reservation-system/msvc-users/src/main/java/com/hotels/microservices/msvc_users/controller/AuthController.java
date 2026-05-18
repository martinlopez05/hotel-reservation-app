package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.dto.*;
import com.hotels.microservices.msvc_users.service.AuthService;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        return authService.register(registerRequestDTO);
    }


    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO authRequestDTO){
        return authService.login(authRequestDTO);
    }

}

