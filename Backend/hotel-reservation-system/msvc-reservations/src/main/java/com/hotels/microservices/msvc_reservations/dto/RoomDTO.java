package com.hotels.microservices.msvc_reservations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private Long id;

    private int roomNumber;

    private Long hotelId;

    private int capacity;

    private Boolean available;

    private int rating;

    private Double pricePerNight;

    private String description;

}
