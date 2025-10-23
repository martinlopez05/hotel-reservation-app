package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.dto.LoginRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.service.IServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IServiceUser serviceUser;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        UserResponseDTO user = serviceUser.login(loginRequestDTO);
        return ResponseEntity.ok(user);
    }
}

