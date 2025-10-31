package com.microservicios.msvc_reviews.service;

import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;

import java.util.List;

public interface IServiceReview {

    List<ReviewResponseDTO> getByHotel(Long hotelId);
    List<ReviewResponseDTO> getAll();
    ReviewResponseDTO getReviewById(Long reviewId);
    ReviewResponseDTO create(ReviewRequestDTO reviewRequestDTO);
    void delete(Long reviewId);
}
