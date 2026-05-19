package com.microservicios.msvc_reviews.controller;

import com.microservicios.msvc_reviews.dto.ReviewRequestDTO;
import com.microservicios.msvc_reviews.dto.ReviewResponseDTO;
import com.microservicios.msvc_reviews.service.IServiceReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
@Tag(name = "Review Controller", description = "Endpoints para la gestión de reseñas y comentarios de los hoteles")
public class ReviewController {

    private final IServiceReview serviceReview;

    @GetMapping
    @Operation(
            summary = "Listar todas las reseñas",
            description = "Retorna un listado global con absolutamente todas las reseñas dejadas por los usuarios en la plataforma."
    )
    public ResponseEntity<List<ReviewResponseDTO>> getAll(){
        return ResponseEntity.ok(serviceReview.getAll());
    }

    @GetMapping("/{hotelId}")
    @Operation(
            summary = "Listar reseñas por Hotel",
            description = "Obtiene todos los comentarios y puntuaciones específicos de un hotel filtrando por su ID."
    )
    public ResponseEntity<List<ReviewResponseDTO>> getAllByHotelId(
            @Parameter(description = "ID del hotel del cual se quieren ver las reseñas", example = "1")
            @PathVariable Long hotelId){
        return ResponseEntity.ok(serviceReview.getByHotel(hotelId));
    }

    @PostMapping
    @Operation(
            summary = "Publicar una nueva reseña",
            description = "Permite a un usuario registrar un comentario y una puntuación para un hotel específico."
    )
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody ReviewRequestDTO reviewRequestDTO){
        return new ResponseEntity<>(serviceReview.create(reviewRequestDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una reseña",
            description = "Borra físicamente una reseña del sistema mediante su ID único."
    )
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de la reseña a eliminar", example = "10")
            @PathVariable Long id){
        serviceReview.delete(id);
        return ResponseEntity.noContent().build();
    }

}
