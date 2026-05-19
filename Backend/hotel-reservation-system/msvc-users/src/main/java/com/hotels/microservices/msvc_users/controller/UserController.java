package com.hotels.microservices.msvc_users.controller;

import com.hotels.microservices.msvc_users.config.RabbitConfig;
import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Controller", description = "Endpoints para la gestión completa de usuario y sincronización con otros módulos")
public class UserController {


    private final RabbitTemplate rabbitTemplate;

    private final IUserService serviceUser;

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna una lista con la información básica de todos los usuarios registrados en la plataforma."
    )
    public ResponseEntity<List<UserResponseDTO>>  getAllUsers(){
        return ResponseEntity.ok(serviceUser.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene el perfil detallado de un usuario específico utilizando su identificador único de base de datos."
    )
    public ResponseEntity<UserResponseDTO> getUser(
            @Parameter(description = "ID del usuario a buscar", example = "1")
            @PathVariable Long id){
        return ResponseEntity.ok(serviceUser.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea una cuenta de usuario a partir del cuerpo de la petición. Aplica validaciones sobre campos obligatorios y formatos (como el email)."
    )
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        UserResponseDTO userResponseDTO = serviceUser.create(userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un usuario del sistema",
            description = "Borra físicamente al usuario por su ID y publica de forma asincrónica un evento en RabbitMQ para que otros módulos (como el de reservas o reseñas) limpien en cascada la información vinculada a este ID."
    )
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID del usuario que se va a dar de baja", example = "5")
            @PathVariable Long id){
        serviceUser.delete(id);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                id
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar datos de un usuario",
            description = "Permite modificar los atributos permitidos de un perfil de usuario existente localizándolo mediante su ID."
    )
    public ResponseEntity<UserResponseDTO> editUser(
            @Parameter(description = "ID del usuario que se desea actualizar", example = "1")
            @PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO){
        UserResponseDTO userResponseDTO = serviceUser.edit(userUpdateDTO,id);
        return ResponseEntity.ok(userResponseDTO);
    }

}
