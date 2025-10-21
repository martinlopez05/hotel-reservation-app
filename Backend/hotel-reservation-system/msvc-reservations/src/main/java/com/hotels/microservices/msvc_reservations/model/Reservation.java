package com.hotels.microservices.msvc_reservations.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;
    private Long roomId;
    private Long hotelId;
    private Long userId;
    private Long orderNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double price;
    @CreatedDate
    private LocalDateTime registrationDate;

}
