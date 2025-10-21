package com.hotels.microservices.msvc_hotels.mapper;

import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IHotelMapper {

     HotelDTO toDTO(Hotel hotel);

     Hotel toHotel(HotelDTO hotelDTO);

    @Mapping(target = "id", ignore = true)
    void updateHotelFromDTO(HotelDTO hotelDTO, @MappingTarget Hotel hotel);
}
