package com.hotels.microservices.msvc_hotels.repository;

import com.hotels.microservices.msvc_hotels.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryHotel extends JpaRepository<Hotel,Long> {
}
