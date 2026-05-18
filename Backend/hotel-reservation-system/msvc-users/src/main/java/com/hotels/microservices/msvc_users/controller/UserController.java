package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.config.RabbitConfig;
import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {


    private final RabbitTemplate rabbitTemplate;

    private final IUserService serviceUser;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>>  getAllUsers(){
        return ResponseEntity.ok(serviceUser.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id){
        return ResponseEntity.ok(serviceUser.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        UserResponseDTO userResponseDTO = serviceUser.create(userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        serviceUser.delete(id);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                id
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> editUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO){
        UserResponseDTO userResponseDTO = serviceUser.edit(userUpdateDTO,id);
        return ResponseEntity.ok(userResponseDTO);
    }

}
