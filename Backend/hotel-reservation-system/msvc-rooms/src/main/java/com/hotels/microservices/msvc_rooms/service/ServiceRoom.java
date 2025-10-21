package com.hotels.microservices.msvc_rooms.service;

import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.exception.RoomNotFoundException;
import com.hotels.microservices.msvc_rooms.mapper.IRoomMapper;
import com.hotels.microservices.msvc_rooms.model.Room;
import com.hotels.microservices.msvc_rooms.repository.IRepositoryRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceRoom implements IServiceRoom{

    @Autowired
    IRepositoryRoom repositoryRoom;

    @Autowired
    IRoomMapper roomMapper;

    @Override
    public List<RoomDTO> findAll() {

        List<Room> rooms = repositoryRoom.findAll();

        return rooms.stream().map(r -> roomMapper.toDTO(r)).toList();

    }

    @Override
    public RoomDTO findById(Long id) {
        return repositoryRoom.findById(id).map(roomMapper::toDTO).orElseThrow(()->
                new RoomNotFoundException("Room with id " + id + "not found", "ROOM_NOT_FOUND"));
    }

    @Override
    public List<RoomDTO> findbyHotelId(Long hotelId) {
        return repositoryRoom.findByHotelId(hotelId)
                .stream()
                .map(roomMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public RoomDTO create(RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);

        if(room.getAvailable() == null) {
            room.setAvailable(true);
        }

        Room createdRoom = repositoryRoom.save(room);

        return roomMapper.toDTO(createdRoom);

    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        if(!repositoryRoom.existsById(id)) {
            throw new RoomNotFoundException("Room not found with id " + id, "ROOM_NOT_FOUND");
        }
        repositoryRoom.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteRoomByHotelId(Long hotelId) {
        repositoryRoom.deleteByHotelId(hotelId);
    }

    @Override
    @Transactional
    public RoomDTO editRoom(RoomUpdateDTO roomUpdateDTO, Long id) {
        Room room = repositoryRoom.findById(id).orElseThrow(() ->
                new RoomNotFoundException("Room with id " + id + "not found", "ROOM_NOT_FOUND"));

        roomMapper.updateRoomFromDto(roomUpdateDTO,room);

        Room roomEdit = repositoryRoom.save(room);

        return roomMapper.toDTO(roomEdit);

    }
}
