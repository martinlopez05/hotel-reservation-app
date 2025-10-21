package com.hotels.microservices.msvc_rooms.repository;

import com.hotels.microservices.msvc_rooms.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepositoryRoom extends JpaRepository<Room,Long> {
    List<Room> findByHotelId(long hotelId);
    void deleteByHotelId(Long hotelId);
}
