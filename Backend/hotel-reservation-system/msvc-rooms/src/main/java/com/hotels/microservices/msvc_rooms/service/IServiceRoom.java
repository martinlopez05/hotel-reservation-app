package com.hotels.microservices.msvc_rooms.service;

import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;

import java.util.List;


public interface IServiceRoom {

    List<RoomDTO> findAll();
    RoomDTO findById(Long id);
    List<RoomDTO> findbyHotelId(Long hotelId);
    RoomDTO create(RoomDTO roomDTO);
    void deleteRoom(Long id);
    void deleteRoomByHotelId(Long hotelId);
    RoomDTO editRoom(RoomUpdateDTO roomUpdateDTO, Long id);

}
