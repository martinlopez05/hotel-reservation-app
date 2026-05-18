package com.hotels.microservices.msvc_users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_users.dto.AuthRequestDTO;
import com.hotels.microservices.msvc_users.dto.AuthResponseDTO;
import com.hotels.microservices.msvc_users.dto.RegisterRequestDTO;
import com.hotels.microservices.msvc_users.dto.RegisterResponseDTO;
import com.hotels.microservices.msvc_users.exception.BadCredentialsException;
import com.hotels.microservices.msvc_users.exception.EmailAlreadyExistsException;
import com.hotels.microservices.msvc_users.exception.GlobalExceptionHandler;
import com.hotels.microservices.msvc_users.service.AuthService;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private RegisterRequestDTO registerRequestDTO;
    private RegisterResponseDTO registerResponseDTO;
    private AuthRequestDTO authRequestDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {

        registerRequestDTO = RegisterRequestDTO.builder()
                .username("martin_dev")
                .password("password123")
                .email("martin@mail.com")
                .role("USER")
                .build();

        registerResponseDTO = RegisterResponseDTO.builder()
                .message("User registered successfully!")
                .build();

        authRequestDTO = AuthRequestDTO.builder()
                .username("martin_dev")
                .password("password123")
                .build();

        authResponseDTO = AuthResponseDTO.builder()
                .id(1L)
                .username("martin_dev")
                .email("martin@mail.com")
                .role("ROLE_USER")
                .token("mocked-jwt-token")
                .build();
    }

    @Nested
    class RegisterTests {
        @Test
        void register_ShouldReturn200AndMessage_WhenRequestIsValid() throws Exception {
            when(authService.register(any(RegisterRequestDTO.class))).thenReturn(registerResponseDTO);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully!"));

            verify(authService, times(1)).register(any(RegisterRequestDTO.class));
        }

        @Test
        void register_ShouldReturn400BadRequest_WhenEmailAlreadyExists() throws Exception {
            when(authService.register(any(RegisterRequestDTO.class)))
                    .thenThrow(new EmailAlreadyExistsException("The email is already registered"));

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("The email is already registered"));

            verify(authService, times(1)).register(any(RegisterRequestDTO.class));
        }
    }

    @Nested
    class LoginTests {
        @Test
        void login_ShouldReturn200AndAuthResponseDTO_WhenCredentialsAreCorrect() throws Exception {
            when(authService.login(any(AuthRequestDTO.class))).thenReturn(authResponseDTO);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                    .andExpect(jsonPath("$.username").value("martin_dev"))
                    .andExpect(jsonPath("$.role").value("ROLE_USER"));

            verify(authService, times(1)).login(any(AuthRequestDTO.class));
        }

        @Test
        void login_ShouldReturn401Unauthorized_WhenPasswordIsIncorrect() throws Exception {
            when(authService.login(any(AuthRequestDTO.class)))
                    .thenThrow(new BadCredentialsException("Incorrect password"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Incorrect password"));

            verify(authService, times(1)).login(any(AuthRequestDTO.class));
        }
    }
}