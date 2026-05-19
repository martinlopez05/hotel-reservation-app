package com.hotels.microservices.msvc_reservations.controller;

import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationResponseDTO;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.model.ReservationState;
import com.hotels.microservices.msvc_reservations.service.IServiceReservation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Controller", description = "Endpoints para la creación, consulta y gestión del ciclo de vida de las reservas")
public class ReservationController {


    private final IServiceReservation serviceReservation;

    @GetMapping
    @Operation(
            summary = "Listar todas las reservas",
            description = "Retorna un listado global con el historial de todas las reservas registradas en el sistema."
    )
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(){
        return ResponseEntity.ok(serviceReservation.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar reserva por ID",
            description = "Obtiene los detalles de una reserva específica utilizando su identificador alfanumérico único (UUID)."
    )
    public ResponseEntity<ReservationResponseDTO> getReservation(
            @Parameter(description = "Código único de la reserva (String/UUID)", example = "res-9b1deb4d-3b7d")
            @PathVariable String id){
        return ResponseEntity.ok(serviceReservation.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Listar reservas de un usuario",
            description = "Filtra y retorna todas las reservas asociadas al ID de un usuario específico."
    )
    public ResponseEntity<List<ReservationResponseDTO>> getReservationByUserId(
            @Parameter(description = "ID del usuario para consultar su historial de reservas", example = "12")
            @PathVariable Long userId){
        return ResponseEntity.ok(serviceReservation.findByUserId(userId));
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva reserva",
            description = "Registra una reserva en el sistema a partir de los datos de entrada. Inicializa la reserva en estado pendiente."
    )
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO reservationRequestDTO){
        ReservationResponseDTO reservationResponseDTO = serviceReservation.create(reservationRequestDTO);
        return new ResponseEntity<>(reservationResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar o cancelar una reserva",
            description = "Remueve físicamente el registro de la reserva del sistema mediante su ID único."
    )
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "Código de la reserva que se desea eliminar", example = "res-9b1deb4d-3b7d")
            @PathVariable String id){
        serviceReservation.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/state")
    @Operation(
            summary = "Actualizar el estado de una reserva",
            description = "Modifica el estado actual de la reserva (ej: CONFIRMED, PENDING, CANCELED). Convierte el parámetro de texto a un valor de Enum interno."
    )
    public ResponseEntity<ReservationResponseDTO> updateState(
            @Parameter(description = "Código de la reserva a modificar", example = "res-9b1deb4d-3b7d")
            @PathVariable String id,
            @Parameter(description = "Nuevo estado de la reserva (valores válidos: PENDING, CONFIRMED, CANCELED)", example = "CONFIRMED")
            @RequestParam String state) {
        ReservationState newState = ReservationState.valueOf(state.toUpperCase());

        ReservationResponseDTO updated = serviceReservation.updateState(id, newState);
        return ResponseEntity.ok(updated);
    }

}
