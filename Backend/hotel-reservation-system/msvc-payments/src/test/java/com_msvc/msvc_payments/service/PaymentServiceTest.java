package com_msvc.msvc_payments.service;

import com_msvc.msvc_payments.client.ReservationClient;
import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.dto.ReservationResponseDTO;
import com_msvc.msvc_payments.exception.ExternalServiceException;
import com_msvc.msvc_payments.exception.PaymentNotFoundException;
import com_msvc.msvc_payments.model.Payment;
import com_msvc.msvc_payments.repository.IRepositoryPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private IRepositoryPayment repositoryPayment;

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentRequestDTO paymentRequestDTO;
    private ReservationResponseDTO reservationResponseDTO;
    private final String reservationId = "res-999";

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .userId(10L)
                .reservationId(reservationId)
                .amount(15000.0)
                .status("manual")
                .paymentMethod("credit_card")
                .registrationDate(LocalDateTime.now())
                .build();

        paymentRequestDTO = PaymentRequestDTO.builder()
                .userId(10L)
                .reservationId(reservationId)
                .amount(15000.0)
                .paymentMethod("credit_card")
                .build();

        reservationResponseDTO = ReservationResponseDTO.builder()
                .orderNumber(1L)
                .build();
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_ShouldReturnPaymentsWithOrderNumber_WhenReservationClientWorks() {
            when(repositoryPayment.findAll()).thenReturn(List.of(payment));
            when(reservationClient.getReservation(reservationId))
                    .thenReturn(ResponseEntity.ok(reservationResponseDTO));

            List<PaymentResponseDTO> response = paymentService.findAll();

            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals(1L, response.get(0).getOrderReservation());
            verify(reservationClient, times(1)).getReservation(reservationId);
        }

        @Test
        void findAll_ShouldThrowExternalServiceException_WhenReservationClientFails() {
            when(repositoryPayment.findAll()).thenReturn(List.of(payment));
            when(reservationClient.getReservation(reservationId)).thenThrow(new RuntimeException("Timeout"));

            assertThrows(ExternalServiceException.class, () -> paymentService.findAll());
            verify(repositoryPayment, times(1)).findAll();
        }
    }

    @Nested
    class FindByReservationTests {
        @Test
        void findByReservation_ShouldReturnPayment_WhenPaymentExists() {
            when(repositoryPayment.findByReservationId(reservationId)).thenReturn(Optional.of(payment));

            PaymentResponseDTO response = paymentService.findByReservation(reservationId);

            assertNotNull(response);
            assertEquals(reservationId, response.getReservationId());
            verify(repositoryPayment, times(1)).findByReservationId(reservationId);
        }

        @Test
        void findByReservation_ShouldThrowPaymentNotFoundException_WhenPaymentDoesNotExist() {
            when(repositoryPayment.findByReservationId(reservationId)).thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class, () -> paymentService.findByReservation(reservationId));
            verify(repositoryPayment, times(1)).findByReservationId(reservationId);
        }
    }

    @Nested
    class SaveManualPaymentTests {
        @Test
        void saveManualPayment_ShouldSaveAndReturnResponse() {
            when(repositoryPayment.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO response = paymentService.saveManualPayment(paymentRequestDTO);

            assertNotNull(response);
            assertEquals("manual", response.getStatus());
            verify(repositoryPayment, times(1)).save(any(Payment.class));
        }
    }
}
