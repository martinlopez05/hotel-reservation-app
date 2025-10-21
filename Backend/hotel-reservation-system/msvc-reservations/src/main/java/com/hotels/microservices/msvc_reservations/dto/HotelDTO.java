package com.hotels.microservices.msvc_reservations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    private Long id;

    private String name;

    private String address;

    private String country;

    private String location;

    private String description;

    private String imageUrl;

    private Integer rating;

    private String email;

    private String phone;

    private List<RoomDTO> roomDTOS;

}
