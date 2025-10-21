package com.hotels.microservices.msvc_reservations.client;

import com.hotels.microservices.msvc_reservations.dto.HotelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-hotels", url = "localhost:8002")
public interface HotelClientRest {

    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelDTO> getHotel(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean includeRooms);
}
