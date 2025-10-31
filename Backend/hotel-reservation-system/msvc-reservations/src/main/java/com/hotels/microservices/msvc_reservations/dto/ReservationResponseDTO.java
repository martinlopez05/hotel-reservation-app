package com.hotels.microservices.msvc_reservations.dto;

import com.hotels.microservices.msvc_reservations.model.ReservationState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {
    private String id;
    private String hotelName;
    private int roomNumber;
    private Long orderNumber;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReservationState state;

    private String username;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double price;
    private LocalDateTime registrationDate;
}
