package com.microservicios.msvc_reviews.service;

import com.microservicios.msvc_reviews.client.UserClientRest;
import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.dto.UserResponseDTO;
import com.microservicios.msvc_reviews.mapper.IReviewMapper;
import com.microservicios.msvc_reviews.model.Review;
import com.microservicios.msvc_reviews.repository.IRepositoryReview;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
public class ServiceReview implements IServiceReview {

    @Autowired
    IRepositoryReview repositoryReview;

    @Autowired
    IReviewMapper reviewMapper;

    @Autowired
    UserClientRest userClientRest;

    @Override
    public List<ReviewResponseDTO> getByHotel(Long hotelId) {
        List<Review> reviews = repositoryReview.findByHotelId(hotelId);

        return reviews.stream()
                .map(r -> {
                    ReviewResponseDTO dto = reviewMapper.toDTO(r);
                    enrichWithUsername(dto, r.getUserId());
                    return dto;
                })
                .toList();
    }


    @Override
    public List<ReviewResponseDTO> getAll() {
        List<Review> reviews = repositoryReview.findAll();

        return reviews.stream()
                .map(r -> {
                    ReviewResponseDTO dto = reviewMapper.toDTO(r);
                    enrichWithUsername(dto, r.getUserId());
                    return dto;
                })
                .toList();
    }

    @Override
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Review review = repositoryReview.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not exist"));

        ReviewResponseDTO dto = reviewMapper.toDTO(review);
        enrichWithUsername(dto,review.getUserId());
        return dto;
    }

    @Override
    public ReviewResponseDTO create(ReviewRequestDTO reviewRequestDTO) {
        Review review = repositoryReview.save(reviewMapper.toReview(reviewRequestDTO));
        ReviewResponseDTO dto = reviewMapper.toDTO(review);
        enrichWithUsername(dto,review.getUserId());
        return dto;
    }

    @Override
    public void delete(Long reviewId) {
        repositoryReview.deleteById(reviewId);
    }

    private void enrichWithUsername(ReviewResponseDTO dto, Long userId) {
        try {
            UserResponseDTO user = userClientRest.getUser(userId).getBody();
            dto.setUsername(user.getUsername());
        } catch (Exception e) {
            dto.setUsername("Usuario desconocido");
        }

    }
}