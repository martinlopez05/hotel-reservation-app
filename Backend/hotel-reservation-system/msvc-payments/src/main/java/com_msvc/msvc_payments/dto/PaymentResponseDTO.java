package com_msvc.msvc_payments.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long mpPaymentId;
    private Long userId;
    private String reservationId;
    private Long orderReservation;
    private double amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime registrationDate;
}