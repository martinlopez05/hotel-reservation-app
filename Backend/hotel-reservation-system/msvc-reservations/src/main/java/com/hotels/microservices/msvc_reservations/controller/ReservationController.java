package com.hotels.microservices.msvc_reservations.controller;

import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationResponseDTO;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.service.IServiceReservation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    IServiceReservation serviceReservation;

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(){
        return ResponseEntity.ok(serviceReservation.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable String id){
        return ResponseEntity.ok(serviceReservation.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(serviceReservation.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO reservationRequestDTO){
        ReservationResponseDTO reservationResponseDTO = serviceReservation.create(reservationRequestDTO);
        return new ResponseEntity<>(reservationResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable String id){
        serviceReservation.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
