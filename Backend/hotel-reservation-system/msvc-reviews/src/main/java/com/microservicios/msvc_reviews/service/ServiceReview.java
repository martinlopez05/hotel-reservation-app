package com.microservicios.msvc_reviews.service;

import com.microservicios.msvc_reviews.client.UserClientRest;
import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.dto.UserResponseDTO;
import com.microservicios.msvc_reviews.exception.ExternalServiceException;
import com.microservicios.msvc_reviews.exception.ReviewNotFoundException;
import com.microservicios.msvc_reviews.exception.UserNotFoundException;
import com.microservicios.msvc_reviews.mapper.IReviewMapper;
import com.microservicios.msvc_reviews.model.Review;
import com.microservicios.msvc_reviews.repository.IRepositoryReview;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceReview implements IServiceReview {

    private final IRepositoryReview repositoryReview;
    private final IReviewMapper reviewMapper;
    private final UserClientRest userClientRest;

    @Override
    public List<ReviewResponseDTO> getByHotel(Long hotelId) {
        return repositoryReview.findByHotelId(hotelId).stream()
                .map(this::mapAndEnrich)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getAll() {
        return repositoryReview.findAll().stream()
                .map(this::mapAndEnrich)
                .toList();
    }

    @Override
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Review review = repositoryReview.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));
        return mapAndEnrich(review);
    }

    @Override
    public ReviewResponseDTO create(ReviewRequestDTO reviewRequestDTO) {
        Review review = repositoryReview.save(reviewMapper.toReview(reviewRequestDTO));
        return mapAndEnrich(review);
    }

    @Override
    public void delete(Long reviewId) {
        repositoryReview.deleteById(reviewId);
    }

    private ReviewResponseDTO mapAndEnrich(Review review) {
        ReviewResponseDTO dto = reviewMapper.toDTO(review);
        enrichWithUsername(dto, review.getUserId());
        return dto;
    }

    private void enrichWithUsername(ReviewResponseDTO dto, Long userId) {
        try {
            UserResponseDTO user = Optional.ofNullable(userClientRest.getUser(userId).getBody())
                    .orElseThrow(() -> new UserNotFoundException("User data is empty for ID " + userId));

            dto.setUsername(user.getUsername());
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("The user with ID " + userId + " does not exist.");
        } catch (FeignException e) {
            throw new ExternalServiceException("The User service is currently unavailable.");
        }
    }
}