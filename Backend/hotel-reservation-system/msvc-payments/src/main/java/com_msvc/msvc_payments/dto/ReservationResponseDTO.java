package com_msvc.msvc_payments.dto;

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
    private String state;

    private String username;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double price;
    private LocalDateTime registrationDate;
}
