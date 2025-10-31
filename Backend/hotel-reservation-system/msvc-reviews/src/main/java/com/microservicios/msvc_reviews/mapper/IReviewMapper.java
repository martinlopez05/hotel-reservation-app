package com.microservicios.msvc_reviews.mapper;

import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IReviewMapper {

    ReviewResponseDTO toDTO(Review review);

    Review toReview(ReviewRequestDTO reviewRequestDTO);
}
