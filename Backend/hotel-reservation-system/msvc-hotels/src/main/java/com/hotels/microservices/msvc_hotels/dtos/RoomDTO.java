package com.hotels.microservices.msvc_hotels.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private Long id;

    private int roomNumber;

    private Long hotelId;

    private int capacity;

    private String imageUrl;

    private Boolean available;

    private int rating;

    private Double pricePerNight;

    private String description;

}
