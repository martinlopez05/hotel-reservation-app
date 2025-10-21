package com.hotels.microservices.msvc_hotels.service;

import com.hotels.microservices.msvc_hotels.client.RoomClientRest;
import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import com.hotels.microservices.msvc_hotels.mapper.IHotelMapper;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import com.hotels.microservices.msvc_hotels.repository.IRepositoryHotel;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceHotel implements IServiceHotel{

    @Autowired
    IRepositoryHotel repositoryHotel;

    @Autowired
    IHotelMapper hotelMapper;

    @Autowired
    RoomClientRest roomClientRest;

    @Override
    @Transactional
    public List<HotelDTO> findAll() {
        return  repositoryHotel.findAll().stream().map(hotelMapper::toDTO).toList();
    }

    @Override
    public HotelDTO findById(Long id, boolean includeRooms) {
        HotelDTO hotelDTO = repositoryHotel.findById(id)
                .map(hotelMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        if(includeRooms){
            List<RoomDTO> rooms = Optional.ofNullable(roomClientRest.getRoomsByHotel(id).getBody())
                    .orElse(Collections.emptyList());
            hotelDTO.setRoomDTOS(rooms);
        }

        return hotelDTO;
    }

    @Override
    @Transactional
    public HotelDTO create(HotelDTO hotelDTO) {
        Hotel hotel = repositoryHotel.save(hotelMapper.toHotel(hotelDTO));
        return hotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(!repositoryHotel.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found");
        }
        roomClientRest.deleteRoomsByHotel(id);
        repositoryHotel.deleteById(id);
    }

    @Override
    @Transactional
    public HotelDTO edit(HotelDTO hotelDTO, Long id) {
        Hotel hotelEdit = repositoryHotel.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        hotelMapper.updateHotelFromDTO(hotelDTO, hotelEdit);
        Hotel editedHotel = repositoryHotel.save(hotelEdit);
        return  hotelMapper.toDTO(editedHotel);
    }
}
