package com_msvc.msvc_payments.service;

import com_msvc.msvc_payments.client.ReservationClient;
import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.dto.ReservationResponseDTO;
import com_msvc.msvc_payments.exception.ExternalServiceException;
import com_msvc.msvc_payments.exception.PaymentNotFoundException;
import com_msvc.msvc_payments.model.Payment;
import com_msvc.msvc_payments.repository.IRepositoryPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final IRepositoryPayment repositoryPayment;

    private final ReservationClient reservationClient;

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

        return PaymentResponseDTO.builder()
                .id(saved.getId())
                .mpPaymentId(saved.getMpPaymentId())
                .userId(saved.getUserId())
                .reservationId(saved.getReservationId())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .paymentMethod(saved.getPaymentMethod())
                .registrationDate(saved.getRegistrationDate())
                .build();
    }


    @Override
    public List<PaymentResponseDTO> findAll() {
        List<Payment> payments = repositoryPayment.findAll();
        List<PaymentResponseDTO> paymentsResponse = new ArrayList<>();



        for(Payment payment : payments){
            ReservationResponseDTO reservationResponseDTO;

            try {
                reservationResponseDTO = reservationClient.getReservation(payment.getReservationId()).getBody();
            } catch (Exception e) {
                throw new ExternalServiceException("Reservation service is currently unavailable.");
            }

            PaymentResponseDTO response = PaymentResponseDTO.builder()
                    .id(payment.getId())
                    .mpPaymentId(payment.getMpPaymentId())
                    .userId(payment.getUserId())
                    .reservationId(payment.getReservationId())
                    .orderReservation(reservationResponseDTO.getOrderNumber())
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
        Payment payment = repositoryPayment.findByReservationId(reservationId).orElseThrow(()-> new PaymentNotFoundException("Payment not found"));

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
