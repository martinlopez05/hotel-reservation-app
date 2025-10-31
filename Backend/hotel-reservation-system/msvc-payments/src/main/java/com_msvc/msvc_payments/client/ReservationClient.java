package com_msvc.msvc_payments.client;

import com_msvc.msvc_payments.dto.ReservationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-reservations", url = "localhost:8003/reservations")
public interface ReservationClient {

    @GetMapping("/{id}")
    ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable String id);


    @PutMapping("/{id}/state")
    ReservationResponseDTO updateState(@PathVariable("id") String id,
                                       @RequestParam("state") String state);
}


