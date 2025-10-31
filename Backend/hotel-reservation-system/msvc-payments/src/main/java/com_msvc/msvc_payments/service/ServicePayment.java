package com_msvc.msvc_payments.service;

import com_msvc.msvc_payments.client.ReservationClient;
import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.model.Payment;
import com_msvc.msvc_payments.repository.IRepositoryPayment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicePayment implements IServicePayment {

    @Autowired
    IRepositoryPayment repositoryPayment;

    @Autowired
    ReservationClient reservationClient;

    public void savePaymentFromMP(com.mercadopago.resources.payment.Payment mpPayment) {

        String[] parts = mpPayment.getExternalReference().split(":");
        Long userId = Long.valueOf(parts[0]);
        String reservationId = parts[1];



        Payment entity = Payment.builder()
                .mpPaymentId(mpPayment.getId())
                .reservationId(reservationId)
                .amount(mpPayment.getTransactionAmount().doubleValue())
                .status(mpPayment.getStatus())
                .paymentMethod(mpPayment.getPaymentMethodId())
                .registrationDate(LocalDateTime.now())
                .userId(userId)
                .build();

        repositoryPayment.save(entity);
    }

    public PaymentResponseDTO saveManualPayment(PaymentRequestDTO dto) {
        Payment entity = Payment.builder()
                .userId(dto.getUserId())
                .reservationId(dto.getReservationId())
                .amount(dto.getAmount())
                .status("manual")
                .paymentMethod(dto.getPaymentMethod())
                .registrationDate(LocalDateTime.now())
                .build();

        Payment saved = repositoryPayment.save(entity);

        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .id(saved.getId())
                .mpPaymentId(saved.getMpPaymentId())
                .userId(saved.getUserId())
                .reservationId(saved.getReservationId())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .paymentMethod(saved.getPaymentMethod())
                .registrationDate(saved.getRegistrationDate())
                .build();

        return response;
    }


    @Override
    public List<PaymentResponseDTO> findAll() {
        List<Payment> payments = repositoryPayment.findAll();
        List<PaymentResponseDTO> paymentsResponse = new ArrayList<>();

        for(Payment payment : payments){
            PaymentResponseDTO response = PaymentResponseDTO.builder()
                    .id(payment.getId())
                    .mpPaymentId(payment.getMpPaymentId())
                    .userId(payment.getUserId())
                    .reservationId(payment.getReservationId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .paymentMethod(payment.getPaymentMethod())
                    .registrationDate(payment.getRegistrationDate())
                    .build();

            if (payment.getMpPaymentId() != null) {
                response.setMpPaymentId(payment.getMpPaymentId());
            }

            paymentsResponse.add(response);

        }

        return paymentsResponse;


    }



    public PaymentResponseDTO findByReservation(String reservationId) {
        Payment payment = repositoryPayment.findByReservationId(reservationId).orElseThrow(()-> new EntityNotFoundException("Payment not exist"));

        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .id(payment.getId())
                .mpPaymentId(payment.getMpPaymentId())
                .userId(payment.getUserId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .registrationDate(payment.getRegistrationDate())
                .build();

        if (payment.getMpPaymentId() != null) {
            response.setMpPaymentId(payment.getMpPaymentId());
        }
        return response;
    }

    @Override
    public void editReservation(String reservationId, String state) {
        reservationClient.updateState(reservationId, state);
    }
}
