package com_msvc.msvc_payments.repository;

import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRepositoryPayment extends JpaRepository<Payment,Long> {
    Optional<Payment> findByReservationId(String reservationId);
}
