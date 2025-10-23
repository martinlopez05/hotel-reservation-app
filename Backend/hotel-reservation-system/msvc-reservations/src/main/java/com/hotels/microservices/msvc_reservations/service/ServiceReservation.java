package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.Listener.UserListener;
import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.RoomCilentRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.*;
import com.hotels.microservices.msvc_reservations.mapper.IReservationMapper;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceReservation implements IServiceReservation{


    @Autowired
    IRepositoryReservation repositoryReservation;

    @Autowired
    IReservationMapper reservationMapper;

    @Autowired
    RoomCilentRest roomClientRest;

    @Autowired
    HotelClientRest hotelClientRest;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    UserClientRest userClientRest;

    @Autowired
    UserListener userListener;

    @Override
    public ReservationResponseDTO create(ReservationRequestDTO reservationRequestDTO) {

        boolean exists = repositoryReservation
                .existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        reservationRequestDTO.getRoomId(),
                        reservationRequestDTO.getCheckOutDate(),
                        reservationRequestDTO.getCheckInDate()
                );

        if (reservationRequestDTO.getCheckOutDate() == null
                || reservationRequestDTO.getCheckOutDate().isEqual(reservationRequestDTO.getCheckInDate())) {
            reservationRequestDTO.setCheckOutDate(reservationRequestDTO.getCheckInDate());
        }

        long days = ChronoUnit.DAYS.between(reservationRequestDTO.getCheckInDate(), reservationRequestDTO.getCheckOutDate());
        if (days == 0){
            days = 1;
        }

        if (Boolean.TRUE.equals(exists)) {
            throw new RuntimeException("Room is  reservated  on  the dates");
        }

        RoomDTO roomDTO = roomClientRest.getRoom(reservationRequestDTO.getRoomId()).getBody();

        Reservation reservation = reservationMapper.toReservation(reservationRequestDTO);

        reservation.setPrice(roomDTO.getPricePerNight() * days);
        reservation.setOrderNumber(sequenceGenerator.generateSequence("reservationOrder"));

        repositoryReservation.save(reservation);

        ReservationResponseDTO reservationResponseDTO = reservationMapper.toReservationResponse(reservation);
        enrichReservationDTO(reservationResponseDTO,reservation);

        return reservationResponseDTO;

    }

    @Override
    public List<ReservationResponseDTO> findAll() {
        List<Reservation> reservations = repositoryReservation.findAll();
        List<ReservationResponseDTO> responseList = new ArrayList<>();

        for (Reservation r : reservations) {
            ReservationResponseDTO dto = reservationMapper.toReservationResponse(r);
            enrichReservationDTO(dto,r);
            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public ReservationResponseDTO findById(String id) {
        Reservation reservation = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        ReservationResponseDTO dto = reservationMapper.toReservationResponse(reservation);

        enrichReservationDTO(dto,reservation);

        return dto;
    }

    @Override
    public List<ReservationResponseDTO> findByUserId(Long userId) {
        List<Reservation> reservations = repositoryReservation.findByUserId(userId);
        List<ReservationResponseDTO> responseList = new ArrayList<>();

        for (Reservation r : reservations) {
            ReservationResponseDTO dto = reservationMapper.toReservationResponse(r);
            enrichReservationDTO(dto,r);
            responseList.add(dto);
        }

        return responseList;

    }

    @Override
    public void deleteById(String id) {
        if(!repositoryReservation.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found");
        }
        repositoryReservation.deleteById(id);
    }



    private void enrichReservationDTO(ReservationResponseDTO dto, Reservation reservation) {
        RoomDTO roomDTO = roomClientRest.getRoom(reservation.getRoomId()).getBody();
        HotelDTO hotelDTO = hotelClientRest.getHotel(reservation.getHotelId(), false).getBody();
        UserDTO userDTO = userClientRest.getUser(reservation.getUserId()).getBody();

        dto.setRoomNumber(roomDTO != null ? roomDTO.getRoomNumber() : 0);
        dto.setHotelName(hotelDTO != null ? hotelDTO.getName() : null);
        dto.setUsername(userDTO != null ? userDTO.getUsername() : null);
    }


}
