package com.hotels.microservices.msvc_hotels.client;

import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-rooms", url = "localhost:8001")
public interface RoomClientRest {

    @GetMapping("/rooms/hotel/{hotelId}")
    ResponseEntity<List<RoomDTO>> getRoomsByHotel(@PathVariable Long hotelId);

    @PostMapping("/rooms")
    ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO);

    @DeleteMapping("/rooms/hotel/{hotelId}")
    ResponseEntity<?> deleteRoomsByHotel(@PathVariable Long hotelId);

}
