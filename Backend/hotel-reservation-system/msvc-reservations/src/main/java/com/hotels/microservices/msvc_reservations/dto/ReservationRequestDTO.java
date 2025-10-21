package com.hotels.microservices.msvc_reservations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {

    @NotNull(message = "roomId is null")
    private Long roomId;

    @NotNull(message = "hotelId is null")
    private Long hotelId;

    @NotNull(message = "userId is null")
    private Long userId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

}
