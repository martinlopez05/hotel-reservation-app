package com.hotels.microservices.msvc_reservations.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
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

    @NotNull(message = "checkInDate is required")
    @FutureOrPresent(message = "checkInDate must be today or in the future")
    private LocalDate checkInDate;

    @FutureOrPresent(message = "checkOutDate must be today or in the future")
    private LocalDate checkOutDate;

}
