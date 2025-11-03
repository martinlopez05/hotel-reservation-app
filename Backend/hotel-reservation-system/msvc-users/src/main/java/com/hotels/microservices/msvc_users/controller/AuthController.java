package com.hotels.microservices.msvc_users.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hotels.microservices.msvc_users.dto.*;
import com.hotels.microservices.msvc_users.service.AuthService;
import com.hotels.microservices.msvc_users.service.IServiceUser;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    AuthService authService;

    @Autowired
    JwtUtil jwtUtil;


    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        return authService.register(registerRequestDTO);
    }


    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO authRequestDTO){
        return authService.login(authRequestDTO);
    }



}

