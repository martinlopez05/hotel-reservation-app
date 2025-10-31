package com.microservicios.msvc_reviews.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {

    private Long id;

    private String username;

    private int rating;

    private String commentary;

    private LocalDateTime createdAt;
}
