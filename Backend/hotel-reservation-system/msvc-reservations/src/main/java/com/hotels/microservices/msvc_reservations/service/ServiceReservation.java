package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.RoomClientRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.*;
import com.hotels.microservices.msvc_reservations.exception.*;
import com.hotels.microservices.msvc_reservations.mapper.IReservationMapper;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.model.ReservationState;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceReservation implements IServiceReservation{

    private final IRepositoryReservation repositoryReservation;


    private final IReservationMapper reservationMapper;


    private final RoomClientRest roomClientRest;


    private final HotelClientRest hotelClientRest;


    private final SequenceGeneratorService sequenceGenerator;


    private final UserClientRest userClientRest;


    @Override
    public ReservationResponseDTO create(ReservationRequestDTO reservationRequestDTO) {

        long days = sanitizeDatesAndCalculateDays(reservationRequestDTO);

        validateRoomAvailability(reservationRequestDTO);

        RoomDTO roomDTO = getRoomData(reservationRequestDTO.getRoomId());

        Reservation reservation = reservationMapper.toReservation(reservationRequestDTO);

        reservation.setPrice(roomDTO.getPricePerNight() * days);
        reservation.setOrderNumber(sequenceGenerator.generateSequence("reservationOrder"));
        reservation.setState(ReservationState.PENDING);
        repositoryReservation.save(reservation);

        ReservationResponseDTO reservationResponseDTO = reservationMapper.toReservationResponse(reservation);
        enrichReservationDTO(reservationResponseDTO,reservation);

        return reservationResponseDTO;

    }

    @Override
    public List<ReservationResponseDTO> findAll() {
        return repositoryReservation.findAll().stream()
                .map(r -> {
                    ReservationResponseDTO dto = reservationMapper.toReservationResponse(r);
                    enrichReservationDTO(dto, r);
                    return dto;
                }).toList();

    }



    @Override
    public ReservationResponseDTO findById(String id) {
        Reservation reservation = repositoryReservation.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        ReservationResponseDTO dto = reservationMapper.toReservationResponse(reservation);

        enrichReservationDTO(dto,reservation);

        return dto;
    }

    @Override
    public List<ReservationResponseDTO> findByUserId(Long userId) {
        return repositoryReservation.findByUserId(userId).stream()
                .map(r -> {
                    ReservationResponseDTO dto = reservationMapper.toReservationResponse(r);
                    enrichReservationDTO(dto,r);
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteById(String id) {
        if(!repositoryReservation.existsById(id)) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        repositoryReservation.deleteById(id);
    }


    @Override
    public ReservationResponseDTO updateState(String reservationId, ReservationState newState) {
        Reservation reservation = repositoryReservation.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        reservation.setState(newState);
        repositoryReservation.save(reservation);

        ReservationResponseDTO dto = reservationMapper.toReservationResponse(reservation);
        enrichReservationDTO(dto, reservation);
        return dto;
    }


    private void enrichReservationDTO(ReservationResponseDTO dto, Reservation reservation) {
        RoomDTO roomDTO = getRoomData(reservation.getRoomId());
        HotelDTO hotelDTO = getHotelData(reservation.getHotelId());
        UserDTO userDTO = getUserData(reservation.getUserId());

        dto.setRoomNumber(roomDTO.getRoomNumber());
        dto.setHotelName(hotelDTO.getName());
        dto.setUsername(userDTO.getUsername());
    }

    private void validateRoomAvailability(ReservationRequestDTO dto){
        boolean isReserved = repositoryReservation
                .existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        dto.getRoomId(),
                        dto.getCheckOutDate(),
                        dto.getCheckInDate()
                );

        if(isReserved){
            throw new RoomIsReservedException("Room is reserved on the dates");
        }
    }

    private long sanitizeDatesAndCalculateDays(ReservationRequestDTO dto) {
        if (dto.getCheckOutDate() == null || dto.getCheckOutDate().isEqual(dto.getCheckInDate())) {
            dto.setCheckOutDate(dto.getCheckInDate());
        }

        long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        return days == 0 ? 1 : days;
    }

    private RoomDTO getRoomData(Long roomId) {
        try {
            return roomClientRest.getRoom(roomId).getBody();

        } catch (FeignException.NotFound e) {
            throw new RoomNotFoundException("The room with ID " + roomId + " not exist.");

        } catch (FeignException e) {
            throw new ExternalServiceException("The service Room is not availabity.");
        }
    }

    private HotelDTO getHotelData(Long hotelId) {
        try {
            return hotelClientRest.getHotel(hotelId,false).getBody();

        } catch (FeignException.NotFound e) {
            throw new HotelNotFoundException("The hotel with ID " + hotelId + " not exist.");

        } catch (FeignException e) {
            throw new ExternalServiceException("The service Hotel is not availabity.");
        }
    }

    private UserDTO getUserData(Long userId) {
        try {
            return userClientRest.getUser(userId).getBody();

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("The user with ID " + userId + " not exist.");

        } catch (FeignException e) {
            throw new ExternalServiceException("The service User is not availabity.");
        }
    }


}
