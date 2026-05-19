package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.dto.*;
import com.hotels.microservices.msvc_users.service.AuthService;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Endpoints para el registro de nuevos usuarios y generación de tokens de seguridad JWT")
public class AuthController {

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar un nuevo usuario en la plataforma",
            description = "Crea una cuenta de usuario con credenciales de acceso. Encripta la contraseña de forma segura y devuelve los datos básicos del perfil creado."
    )
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        return authService.register(registerRequestDTO);
    }


    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión y obtener token JWT",
            description = "Autentica las credenciales del usuario (email y contraseña). Si son válidas, genera y retorna un token JWT que deberá incluirse en la cabecera 'Authorization: Bearer <token>' para las peticiones a endpoints protegidos."
    )
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO authRequestDTO){
        return authService.login(authRequestDTO);
    }

}

