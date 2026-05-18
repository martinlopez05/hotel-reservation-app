package com.microservicios.msvc_reviews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.exception.ReviewNotFoundException;
import com.microservicios.msvc_reviews.service.IServiceReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IServiceReview serviceReview;

    private ReviewResponseDTO responseDTO;
    private ReviewRequestDTO requestDTO;
    private final Long reviewId = 100L;
    private final Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = ReviewRequestDTO.builder()
                .hotelId(hotelId)
                .userId(5L)
                .rating(5)
                .commentary("Excelente hotel y servicio")
                .build();

        responseDTO = ReviewResponseDTO.builder()
                .id(reviewId)
                .username("martin_dev")
                .rating(5)
                .commentary("Excelente hotel y servicio")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    class GetAllTests {
        @Test
        void getAll_ShouldReturnListOfReviews() throws Exception {
            List<ReviewResponseDTO> list = List.of(responseDTO);
            when(serviceReview.getAll()).thenReturn(list);

            mockMvc.perform(get("/review"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("martin_dev"))
                    .andExpect(jsonPath("$[0].commentary").value("Excelente hotel y servicio"));

            verify(serviceReview, times(1)).getAll();
        }
    }

    @Nested
    class GetAllByHotelIdTests {
        @Test
        void getAllByHotelId_ShouldReturnReviewsForSpecificHotel() throws Exception {
            List<ReviewResponseDTO> list = List.of(responseDTO);
            when(serviceReview.getByHotel(hotelId)).thenReturn(list);

            mockMvc.perform(get("/review/{hotelId}", hotelId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].username").value("martin_dev"))
                    .andExpect(jsonPath("$[0].commentary").value("Excelente hotel y servicio"));

            verify(serviceReview, times(1)).getByHotel(hotelId);
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldReturn201AndCreatedReview() throws Exception {
            when(serviceReview.create(any(ReviewRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/review")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(reviewId))
                    .andExpect(jsonPath("$.username").value("martin_dev"));

            verify(serviceReview, times(1)).create(any(ReviewRequestDTO.class));
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void deleteById_ShouldReturn200_WhenSuccessful() throws Exception {
            mockMvc.perform(delete("/review/{id}", reviewId))
                    .andExpect(status().isNoContent());

            verify(serviceReview, times(1)).delete(reviewId);
        }

        @Test
        void deleteById_ShouldReturn404_WhenReviewDoesNotExist() throws Exception {
            doThrow(new ReviewNotFoundException("Review not found")).when(serviceReview).delete(reviewId);

            mockMvc.perform(delete("/review/{id}", reviewId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Review not found"));

            verify(serviceReview, times(1)).delete(reviewId);
        }
    }
}
