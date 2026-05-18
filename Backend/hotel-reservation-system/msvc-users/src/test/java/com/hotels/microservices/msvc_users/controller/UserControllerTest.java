package com.hotels.microservices.msvc_users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.exception.GlobalExceptionHandler;
import com.hotels.microservices.msvc_users.exception.UserNotFoundException;
import com.hotels.microservices.msvc_users.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService serviceUser;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;
    private UserUpdateDTO userUpdateDTO;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {

        userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .username("martin_dev")
                .email("martin@mail.com")
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .username("martin_dev")
                .password("password123")
                .email("martin@mail.com")
                .build();

        userUpdateDTO = UserUpdateDTO.builder()
                .username("martin_updated")
                .password("newpassword123")
                .email("martin_updated@mail.com")
                .build();
    }

    @Nested
    class GetAllUsersTests {
        @Test
        void getAllUsers_ShouldReturnListOfUsers() throws Exception {
            List<UserResponseDTO> users = List.of(userResponseDTO);
            when(serviceUser.findAll()).thenReturn(users);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("martin_dev"));

            verify(serviceUser, times(1)).findAll();
        }
    }

    @Nested
    class GetUserTests {
        @Test
        void getUser_WhenUserExists_ShouldReturnUser() throws Exception {
            when(serviceUser.findById(userId)).thenReturn(userResponseDTO);

            mockMvc.perform(get("/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.username").value("martin_dev"));

            verify(serviceUser, times(1)).findById(userId);
        }

        @Test
        void getUser_WhenUserDoesNotExist_ShouldReturn404() throws Exception {
            when(serviceUser.findById(userId)).thenThrow(new UserNotFoundException("User with id " + userId + " not found"));

            mockMvc.perform(get("/users/{id}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User with id 1 not found"));

            verify(serviceUser, times(1)).findById(userId);
        }
    }

    @Nested
    class CreateUserTests {
        @Test
        void createUser_ShouldReturn201AndCreatedUser() throws Exception {
            when(serviceUser.create(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequestDTO))) // Ahora lleva password!
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("martin_dev"))
                    .andExpect(jsonPath("$.email").value("martin@mail.com"));

            verify(serviceUser, times(1)).create(any(UserRequestDTO.class));
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        void deleteUser_WhenSuccessful_ShouldReturn204AndSendRabbitMessage() throws Exception {
            mockMvc.perform(delete("/users/{id}", userId))
                    .andExpect(status().isNoContent());

            verify(serviceUser, times(1)).delete(userId);
            verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), eq(userId));
        }

        @Test
        void deleteUser_WhenUserDoesNotExist_ShouldReturn404AndNotSendRabbitMessage() throws Exception {
            doThrow(new UserNotFoundException("User with id " + userId + " not found")).when(serviceUser).delete(userId);

            mockMvc.perform(delete("/users/{id}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));

            verify(serviceUser, times(1)).delete(userId);
            verifyNoInteractions(rabbitTemplate);
        }
    }

    @Nested
    class EditUserTests {
        @Test
        void editUser_ShouldReturn200AndEditedUser() throws Exception {
            when(serviceUser.edit(any(UserUpdateDTO.class), eq(userId))).thenReturn(userResponseDTO);

            mockMvc.perform(put("/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.username").value("martin_dev"));

            verify(serviceUser, times(1)).edit(any(UserUpdateDTO.class), eq(userId));
        }
    }
}
