package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.dto.ReservationResponseDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;

import java.util.List;

public interface IServiceReservation {

    ReservationResponseDTO create(ReservationRequestDTO reservationRequestDTO);

    List<ReservationResponseDTO> findAll();

    ReservationResponseDTO findById(String id);

    void deleteById(String id);

    List<ReservationResponseDTO> findByUserId(Long userId);

}
