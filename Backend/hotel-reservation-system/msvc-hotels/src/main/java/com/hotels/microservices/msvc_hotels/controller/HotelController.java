package com.hotels.microservices.msvc_hotels.controller;

import com.hotels.microservices.msvc_hotels.config.RabbitHotelConfig;
import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import com.hotels.microservices.msvc_hotels.service.IServiceHotel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel Controller", description = "Endpoints para la gestión completa de hoteles y sincronización con otros módulos")
public class HotelController {

    private final RabbitTemplate rabbitTemplate;

    private final IServiceHotel serviceHotel;

    @GetMapping
    @Operation(
            summary = "Listar todos los hoteles",
            description = "Retorna una lista con todos los hoteles registrados en el sistema sin discriminar por habitaciones."
    )
    public ResponseEntity<List<HotelDTO>> getAllHotel(){
        return ResponseEntity.ok(serviceHotel.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar un hotel por su ID",
            description = "Permite obtener la información de un hotel específico. Opcionalmente se pueden incluir los detalles de sus habitaciones asociadas."
    )
    public ResponseEntity<HotelDTO> getHotel(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean includeRooms){
        return ResponseEntity.ok(serviceHotel.findById(id,includeRooms));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo hotel",
            description = "Crea un hotel en la base de datos a partir del cuerpo de la petición. Realiza validaciones sobre los datos de entrada."
    )
    public ResponseEntity<HotelDTO> createHotel(@Valid @RequestBody HotelDTO hotelDTO){
        HotelDTO createdHotelDTO = serviceHotel.create(hotelDTO);
        return new ResponseEntity<>(createdHotelDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un hotel",
            description = "Borra físicamente el hotel por su ID y publica un evento en RabbitMQ para que el microservicio de habitaciones (o reservas) limpie los datos huérfanos en cascada."
    )
    public ResponseEntity<?> deleteHotel(@PathVariable Long id){
        serviceHotel.delete(id);
        rabbitTemplate.convertAndSend(
                RabbitHotelConfig.EXCHANGE,
                RabbitHotelConfig.ROUTING_KEY,
                id
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar datos de un hotel",
            description = "Modifica los atributos de un hotel existente localizándolo mediante su ID único."
    )
    public ResponseEntity<HotelDTO> editHotel(@RequestBody HotelDTO hotelDTO, @PathVariable Long id){
        HotelDTO editedHotelDTO = serviceHotel.edit(hotelDTO,id);
        return ResponseEntity.ok(editedHotelDTO);
    }


}
