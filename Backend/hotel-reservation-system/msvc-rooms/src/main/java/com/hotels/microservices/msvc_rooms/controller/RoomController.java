package com.hotels.microservices.msvc_rooms.controller;

import com.hotels.microservices.msvc_rooms.config.RabbitRoomConfig;
import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.model.Room;
import com.hotels.microservices.msvc_rooms.service.IServiceRoom;
import jakarta.validation.Valid;
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
public class RoomController {

    @Autowired
    IServiceRoom serviceRoom;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms(){
        return ResponseEntity.ok(serviceRoom.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long id){
        return ResponseEntity.ok(serviceRoom.findById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(serviceRoom.findbyHotelId(hotelId));
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom( @Valid @RequestBody RoomDTO roomDTO){
        RoomDTO createdRoom = serviceRoom.create(roomDTO);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id){
        serviceRoom.deleteRoom(id);
        rabbitTemplate.convertAndSend(
                RabbitRoomConfig.EXCHANGE,
                RabbitRoomConfig.ROUTING_KEY,
                id
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<?> deleteRoomsByHotel(@PathVariable Long hotelId){
        serviceRoom.deleteRoomByHotelId(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> editRoom(@PathVariable Long id, @Valid @RequestBody RoomUpdateDTO roomUpdateDTO){
        RoomDTO editedRoom = serviceRoom.editRoom(roomUpdateDTO,id);
        return ResponseEntity.ok(editedRoom);
    }



}
