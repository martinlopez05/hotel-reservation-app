package com.microservicios.msvc_reviews.controller;

import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.service.IServiceReview;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final IServiceReview serviceReview;

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAll(){
        return ResponseEntity.ok(serviceReview.getAll());
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<List<ReviewResponseDTO>> getAllByHotelId(@PathVariable Long hotelId){
        return ResponseEntity.ok(serviceReview.getByHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody ReviewRequestDTO reviewRequestDTO){
        return new ResponseEntity<>(serviceReview.create(reviewRequestDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        serviceReview.delete(id);
        return ResponseEntity.noContent().build();
    }

}
