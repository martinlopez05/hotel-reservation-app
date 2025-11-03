package com.hotels.microservices.msvc_reservations.client;

import com.hotels.microservices.msvc_reservations.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-users")
public interface UserClientRest {

    @GetMapping("/users/{id}")
    ResponseEntity<UserDTO> getUser(@PathVariable Long id);
}
