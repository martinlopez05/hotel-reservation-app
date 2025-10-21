package com.hotels.microservices.msvc_rooms.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private Long id;

    @NotNull(message = "roomNumber is null")
    private int roomNumber;

    @NotNull(message = "hotelId is null")
    private Long hotelId;

    @NotNull(message = "capacity is null")
    private int capacity;

    private Boolean available;

    @Min(value = 0)
    @Max(value = 5)
    private int rating;

    @NotNull(message = "pricePerNight is null")
    private Double pricePerNight;

    @NotBlank(message = "description is blank")
    private String description;

}
