package com.hotels.microservices.msvc_reservations.mapper;

import com.hotels.microservices.msvc_reservations.dto.ReservationResponseDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IReservationMapper {

    Reservation toReservation(ReservationRequestDTO reservationRequestDTO);

    ReservationResponseDTO toReservationResponse(Reservation reservation);

}
