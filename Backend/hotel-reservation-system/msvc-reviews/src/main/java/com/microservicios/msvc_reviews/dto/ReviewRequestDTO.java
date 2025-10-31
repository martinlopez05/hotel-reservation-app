package com.microservicios.msvc_reviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {

    private Long hotelId;

    private Long userId;

    @Min(1)
    @Max(5)
    private int rating;

    private String commentary;

}
