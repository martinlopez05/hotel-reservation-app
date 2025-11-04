package com.microservicios.msvc_reviews.client;

import com.microservicios.msvc_reviews.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-users")
public interface UserClientRest {

    @GetMapping("/users/{id}")
    ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id);
}
