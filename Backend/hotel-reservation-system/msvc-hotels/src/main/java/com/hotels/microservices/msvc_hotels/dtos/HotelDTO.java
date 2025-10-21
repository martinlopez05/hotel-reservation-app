package com.hotels.microservices.msvc_hotels.dtos;

import jakarta.persistence.Column;
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

    @NotNull
    private String name;

    @NotBlank(message = "address is blank")
    private String address;

    @NotBlank(message = "country is blank")
    private String country;

    @NotBlank(message = "location is blank")
    private String location;

    @NotBlank(message = "Description is blank")
    private String description;

    @NotBlank(message = "ImageUrl is blank")
    private String imageUrl;

    @Min(value = 0)   
    @Max(value = 5)
    private Integer rating;

    @NotBlank(message = "email is blank")
    private String email;

    @NotBlank(message = "phone is blank")
    private String phone;

    private List<RoomDTO> roomDTOS;

}
