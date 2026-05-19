package com.hotels.microservices.msvc_rooms.controller;

import com.hotels.microservices.msvc_rooms.config.RabbitRoomConfig;
import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.model.Room;
import com.hotels.microservices.msvc_rooms.service.IServiceRoom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Controller", description = "Endpoints para la gestión de habitaciones y control de disponibilidad por hotel")
public class RoomController {

    private final IServiceRoom serviceRoom;

    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    @Operation(
            summary = "Listar todas las habitaciones",
            description = "Retorna un listado global con todas las habitaciones registradas en el sistema."
    )
    public ResponseEntity<List<RoomDTO>> getAllRooms(){
        return ResponseEntity.ok(serviceRoom.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar habitación por ID",
            description = "Obtiene los detalles completos de una habitación específica a través de su identificador único."
    )
    public ResponseEntity<RoomDTO> getRoom(
            @Parameter(description = "ID de la habitación a buscar", example = "101")
            @PathVariable Long id){
        return ResponseEntity.ok(serviceRoom.findById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(
            summary = "Listar habitaciones de un hotel",
            description = "Filtra y retorna todas las habitaciones que pertenecen a un hotel específico utilizando el ID del hotel."
    )
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(
            @Parameter(description = "ID del hotel para filtrar sus habitaciones", example = "1")
            @PathVariable Long hotelId){
        return ResponseEntity.ok(serviceRoom.findbyHotelId(hotelId));
    }

    @PostMapping
    @Operation(
            summary = "Registrar una nueva habitación",
            description = "Crea una habitación vinculada a un hotel. Valida que los datos de entrada cumplan con los requisitos del negocio."
    )
    public ResponseEntity<RoomDTO> createRoom( @Valid @RequestBody RoomDTO roomDTO){
        RoomDTO createdRoom = serviceRoom.create(roomDTO);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una habitación por ID",
            description = "Borra físicamente una habitación del sistema y publica un evento en RabbitMQ para notificar el cambio a otros servicios relacionados (como el módulo de reservas)."
    )
    public ResponseEntity<?> deleteRoom(
            @Parameter(description = "ID de la habitación que se desea eliminar", example = "101")
            @PathVariable Long id){
        serviceRoom.deleteRoom(id);
        rabbitTemplate.convertAndSend(
                RabbitRoomConfig.EXCHANGE,
                RabbitRoomConfig.ROUTING_KEY,
                id
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hotel/{hotelId}")
    @Operation(
            summary = "Eliminar todas las habitaciones de un hotel",
            description = "Borra en bloque todas las habitaciones asociadas a un ID de hotel específico. Útil para limpieza de datos cuando un hotel se da de baja."
    )
    public ResponseEntity<?> deleteRoomsByHotel(
            @Parameter(description = "ID del hotel cuyas habitaciones se van a eliminar por completo", example = "1")
            @PathVariable Long hotelId){
        serviceRoom.deleteRoomByHotelId(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar datos de una habitación",
            description = "Modifica los atributos editables de una habitación (como precio o estado) localizándola por su ID único."
    )
    public ResponseEntity<RoomDTO> editRoom(
            @Parameter(description = "ID de la habitación que se quiere modificar", example = "101")
            @PathVariable Long id, @Valid @RequestBody RoomUpdateDTO roomUpdateDTO){
        RoomDTO editedRoom = serviceRoom.editRoom(roomUpdateDTO,id);
        return ResponseEntity.ok(editedRoom);
    }



}
