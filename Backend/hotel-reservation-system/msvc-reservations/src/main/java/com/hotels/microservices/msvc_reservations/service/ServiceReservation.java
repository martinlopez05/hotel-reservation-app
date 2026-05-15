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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.DecimalMax;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceReservation implements IServiceReservation{

    private final IRepositoryReservation repositoryReservation;

    private final IReservationMapper reservationMapper;

    private final SequenceGeneratorService sequenceGenerator;


    private final UserIntegrationService userIntegrationService;

    private final HotelIntegrationService hotelIntegrationService;

    private final RoomIntegrationService roomIntegrationService;


    @Override
    public ReservationResponseDTO create(ReservationRequestDTO reservationRequestDTO) {

        long days = sanitizeDatesAndCalculateDays(reservationRequestDTO);

        UserDTO userDTO = userIntegrationService.getUserData(reservationRequestDTO.getUserId());
        HotelDTO hotelDTO = hotelIntegrationService.getHotelData(reservationRequestDTO.getHotelId());
        RoomDTO roomDTO = roomIntegrationService.getRoomData(reservationRequestDTO.getRoomId());


        validateRoomAvailability(reservationRequestDTO);


        Reservation reservation = reservationMapper.toReservation(reservationRequestDTO);
        reservation.setPrice(roomDTO.getPricePerNight() * days);
        reservation.setOrderNumber(sequenceGenerator.generateSequence("reservationOrder"));
        reservation.setState(ReservationState.PENDING);
        reservation.setUsername(userDTO.getUsername());
        reservation.setHotelName(hotelDTO.getName());
        reservation.setRoomNumber(roomDTO.getRoomNumber());

        Reservation savedReservation = repositoryReservation.save(reservation);

        return reservationMapper.toReservationResponse(savedReservation);
    }

    @Override
    public List<ReservationResponseDTO> findAll() {
        return repositoryReservation.findAll().stream()
                .map(reservationMapper::toReservationResponse).toList();

    }


    @Override
    public ReservationResponseDTO findById(String id) {
        Reservation reservation = repositoryReservation.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation " + id + " not found"));

        return reservationMapper.toReservationResponse(reservation);
    }

    @Override
    public List<ReservationResponseDTO> findByUserId(Long userId) {
        return repositoryReservation.findByUserId(userId).stream()
                .map(reservationMapper::toReservationResponse)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        if(!repositoryReservation.existsById(id)) {
            throw new ReservationNotFoundException("Reservation " + id + " not found");
        }
        repositoryReservation.deleteById(id);
    }


    @Override
    public ReservationResponseDTO updateState(String reservationId, ReservationState newState) {
        Reservation reservation = repositoryReservation.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation " + reservationId + " not found"));

        reservation.setState(newState);
        Reservation savedReservation = repositoryReservation.save(reservation);

        return reservationMapper.toReservationResponse(savedReservation);

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
            dto.setCheckOutDate(dto.getCheckInDate().plusDays(1));
        }

        long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        return days == 0 ? 1 : days;
    }


}
