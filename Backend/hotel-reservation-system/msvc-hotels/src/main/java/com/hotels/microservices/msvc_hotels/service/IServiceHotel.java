package com.hotels.microservices.msvc_hotels.service;

import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IServiceHotel {

    List<HotelDTO> findAll();

    HotelDTO findById(Long id, boolean includeRoom);

    HotelDTO create(HotelDTO hotelDTO);

    void delete(Long id);

    HotelDTO edit(HotelDTO hotelDTO, Long id);


}
