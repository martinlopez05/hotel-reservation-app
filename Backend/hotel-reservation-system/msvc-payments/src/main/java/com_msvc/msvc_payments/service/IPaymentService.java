package com_msvc.msvc_payments.service;

import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;

import java.util.List;

public interface IPaymentService {

    List<PaymentResponseDTO> findAll();
    PaymentResponseDTO findByReservation(String reservationId);
    PaymentResponseDTO saveManualPayment(PaymentRequestDTO dto);
    void savePaymentFromMP(com.mercadopago.resources.payment.Payment mpPayment);
    void editReservation(String reservationId, String state);
}
