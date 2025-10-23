package com.hotels.microservices.msvc_rooms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "number")
    private int roomNumber;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "rating")
    private int rating;

    @Column(name = "price_per_night")
    private Double pricePerNight;

    @Column(name = "description")
    private String description;

}
