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
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceReviewTest {

    @Mock
    private IRepositoryReview repositoryReview;

    @Mock
    private IReviewMapper reviewMapper;

    @Mock
    private UserClientRest userClientRest;

    @InjectMocks
    private ServiceReview serviceReview;

    private Review review;
    private ReviewResponseDTO reviewResponseDTO;
    private ReviewRequestDTO reviewRequestDTO;
    private UserResponseDTO userResponseDTO;

    private final Long reviewId = 100L;
    private final Long hotelId = 1L;
    private final Long userId = 5L;

    @BeforeEach
    void setUp() {

        review = Review.builder()
                .id(reviewId)
                .hotelId(hotelId)
                .userId(userId)
                .rating(5)
                .commentary("Excelente")
                .build();

        reviewRequestDTO = ReviewRequestDTO.builder()
                .hotelId(hotelId)
                .userId(userId)
                .rating(5)
                .commentary("Excelente")
                .build();

        reviewResponseDTO = ReviewResponseDTO.builder()
                .id(reviewId)
                .username("martin_dev")
                .rating(5)
                .commentary("Excelente")
                .createdAt(LocalDateTime.now())
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .username("martin_dev")
                .email("martin@mail.com")
                .build();
    }

    @Nested
    class GetByHotelTests {
        @Test
        void getByHotel_ShouldReturnEnrichedReviews_WhenReviewsExist() {
            when(repositoryReview.findByHotelId(hotelId)).thenReturn(List.of(review));
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);
            when(userClientRest.getUser(userId)).thenReturn(ResponseEntity.ok(userResponseDTO));

            List<ReviewResponseDTO> result = serviceReview.getByHotel(hotelId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("martin_dev", result.get(0).getUsername()); // Validamos el enriquecimiento
            verify(repositoryReview, times(1)).findByHotelId(hotelId);
            verify(userClientRest, times(1)).getUser(userId);
        }
    }

    @Nested
    class GetAllTests {
        @Test
        void getAll_ShouldReturnEnrichedReviewsList() {
            when(repositoryReview.findAll()).thenReturn(List.of(review));
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);
            when(userClientRest.getUser(userId)).thenReturn(ResponseEntity.ok(userResponseDTO));

            List<ReviewResponseDTO> result = serviceReview.getAll();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(repositoryReview, times(1)).findAll();
        }
    }

    @Nested
    class GetReviewByIdTests {
        @Test
        void getReviewById_ShouldReturnReview_WhenExists() {
            when(repositoryReview.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);
            when(userClientRest.getUser(userId)).thenReturn(ResponseEntity.ok(userResponseDTO));

            ReviewResponseDTO result = serviceReview.getReviewById(reviewId);

            assertNotNull(result);
            assertEquals(reviewId, result.getId());
            verify(repositoryReview, times(1)).findById(reviewId);
        }

        @Test
        void getReviewById_ShouldThrowReviewNotFoundException_WhenDoesNotExist() {
            when(repositoryReview.findById(reviewId)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class, () -> serviceReview.getReviewById(reviewId));

            verify(repositoryReview, times(1)).findById(reviewId);
            verifyNoInteractions(userClientRest);
            verifyNoInteractions(reviewMapper);
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldSaveAndReturnEnrichedReview() {
            when(reviewMapper.toReview(reviewRequestDTO)).thenReturn(review);
            when(repositoryReview.save(review)).thenReturn(review);
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);
            when(userClientRest.getUser(userId)).thenReturn(ResponseEntity.ok(userResponseDTO));

            ReviewResponseDTO result = serviceReview.create(reviewRequestDTO);

            assertNotNull(result);
            assertEquals("martin_dev", result.getUsername());
            verify(repositoryReview, times(1)).save(review);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_ShouldCallRepositoryDelete() {
            serviceReview.delete(reviewId);

            verify(repositoryReview, times(1)).deleteById(reviewId);
        }
    }

    @Nested
    class FeignExceptionTests {

        private FeignException createFeignException(int status) {
            return FeignException.errorStatus(
                    "getUser",
                    feign.Response.builder()
                            .status(status)
                            .reason("Error")
                            .request(Request.create(Request.HttpMethod.GET, "/url", Map.of(), null, null, null))
                            .build()
            );
        }

        @Test
        void enrichWithUsername_ShouldThrowUserNotFoundException_WhenFeignReturns404() {
            when(repositoryReview.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);

            // Forzamos un FeignException.NotFound (Código 404)
            FeignException.NotFound feignNotFound = (FeignException.NotFound) createFeignException(404);
            when(userClientRest.getUser(userId)).thenThrow(feignNotFound);

            assertThrows(UserNotFoundException.class, () -> serviceReview.getReviewById(reviewId));

            verify(repositoryReview, times(1)).findById(reviewId);
            verify(userClientRest, times(1)).getUser(userId);
        }

        @Test
        void enrichWithUsername_ShouldThrowExternalServiceException_WhenFeignReturnsOtherError() {
            when(repositoryReview.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewMapper.toDTO(review)).thenReturn(reviewResponseDTO);

            FeignException feignInternalError = createFeignException(500);
            when(userClientRest.getUser(userId)).thenThrow(feignInternalError);

            assertThrows(ExternalServiceException.class, () -> serviceReview.getReviewById(reviewId));

            verify(repositoryReview, times(1)).findById(reviewId);
            verify(userClientRest, times(1)).getUser(userId);
        }
    }
}

