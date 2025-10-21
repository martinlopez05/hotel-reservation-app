package com.hotels.microservices.msvc_reservations.client;

import com.hotels.microservices.msvc_reservations.dto.RoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-rooms", url = "localhost:8001")
public interface RoomCilentRest {

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long id);
}
