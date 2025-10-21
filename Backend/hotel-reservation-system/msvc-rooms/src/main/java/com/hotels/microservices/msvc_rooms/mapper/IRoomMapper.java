package com.hotels.microservices.msvc_rooms.mapper;

import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IRoomMapper {

    RoomDTO toDTO(Room room);

    Room toEntity(RoomDTO roomDTO);

    void updateRoomFromDto(RoomUpdateDTO dto, @MappingTarget Room room);
}
