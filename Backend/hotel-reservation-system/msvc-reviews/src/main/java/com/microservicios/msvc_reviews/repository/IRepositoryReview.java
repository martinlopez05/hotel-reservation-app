package com.microservicios.msvc_reviews.repository;

import com.microservicios.msvc_reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepositoryReview extends JpaRepository<Review,Long> {

    List<Review> findByHotelId(Long hotelId);
}
