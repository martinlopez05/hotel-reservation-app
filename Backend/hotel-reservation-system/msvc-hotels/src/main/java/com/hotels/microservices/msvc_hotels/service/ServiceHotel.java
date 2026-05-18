package com.hotels.microservices.msvc_hotels.service;

import com.hotels.microservices.msvc_hotels.client.RoomClientRest;
import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import com.hotels.microservices.msvc_hotels.exception.ExternalServiceException;
import com.hotels.microservices.msvc_hotels.exception.HotelNotFoundException;
import com.hotels.microservices.msvc_hotels.mapper.IHotelMapper;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import com.hotels.microservices.msvc_hotels.repository.IRepositoryHotel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceHotel implements IServiceHotel{


    private final IRepositoryHotel repositoryHotel;

    private final IHotelMapper hotelMapper;

    private final RoomClientRest roomClientRest;

    @Override
    @Transactional
    public List<HotelDTO> findAll() {
        return  repositoryHotel.findAll().stream().map(hotelMapper::toDTO).toList();
    }

    @Override
    public HotelDTO findById(Long id, boolean includeRooms) {
        HotelDTO hotelDTO = repositoryHotel.findById(id)
                .map(hotelMapper::toDTO)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with id " + id + " not found"));

        if (includeRooms) {
            try {
                List<RoomDTO> rooms = Optional.ofNullable(roomClientRest.getRoomsByHotel(id).getBody())
                        .orElse(Collections.emptyList());
                hotelDTO.setRoomDTOS(rooms);
            } catch (Exception e) {
                throw new ExternalServiceException("Room service is temporarily unavailable");
            }
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
            throw new HotelNotFoundException("Hotel with id " + id + " not found");
        }
        repositoryHotel.deleteById(id);
    }

    @Override
    @Transactional
    public HotelDTO edit(HotelDTO hotelDTO, Long id) {
        Hotel hotelEdit = repositoryHotel.findById(id).orElseThrow(() -> new HotelNotFoundException("Hotel with id " + id + " not found"));
        hotelMapper.updateHotelFromDTO(hotelDTO, hotelEdit);
        Hotel editedHotel = repositoryHotel.save(hotelEdit);
        return  hotelMapper.toDTO(editedHotel);
    }
}
