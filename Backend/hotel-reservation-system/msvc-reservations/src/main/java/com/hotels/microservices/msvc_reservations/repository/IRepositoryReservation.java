package com.hotels.microservices.msvc_reservations.repository;

import com.hotels.microservices.msvc_reservations.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IRepositoryReservation extends MongoRepository<Reservation,String> {

    boolean existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long roomId,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );

    void deleteAllByUserId(Long userId);

    void deleteAllByHotelId(Long hotelId);

    List<Reservation> findByUserId(Long userId);

}
