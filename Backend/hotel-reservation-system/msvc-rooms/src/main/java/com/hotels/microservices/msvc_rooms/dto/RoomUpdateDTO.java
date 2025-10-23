package com.hotels.microservices.msvc_rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomUpdateDTO {
    private Integer roomNumber;
    private Integer capacity;
    private String imageUrl;
    private Boolean available;
    private Integer rating;
    private Double pricePerNight;
    private String description;
}
