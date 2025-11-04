package com_msvc.msvc_payments.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.dto.ReservationResponseDTO;
import com_msvc.msvc_payments.model.Payment;
import com_msvc.msvc_payments.service.IServicePayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PreferenceClient preferenceClient;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Autowired
    IServicePayment servicePayment;


    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getPayments() {
        return ResponseEntity.ok(servicePayment.findAll());
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponseDTO> getByReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(servicePayment.findByReservation(reservationId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createManualPayment(@RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO paymentResponseDTO = servicePayment.saveManualPayment(dto);
        servicePayment.editReservation(paymentResponseDTO.getReservationId(), "PAYMENT");
        return ResponseEntity.ok(paymentResponseDTO);
    }


    @PostMapping("/mercadopago")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequestDTO request) {
        try {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Reserva #" + request.getReservationId())
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(BigDecimal.valueOf(request.getAmount()))
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .externalReference(request.getUserId() + ":" + request.getReservationId())
                    .notificationUrl("https://vps-5440117-x.dattaweb.com/payment/webhook")
                    .backUrls(
                            PreferenceBackUrlsRequest.builder()
                                    .success("https://hotelfly.netlify.app")
                                    .failure("https://hotelfly.netlify.app")
                                    .pending("https://hotelfly.netlify.app/")
                                    .build()
                    )
                    .autoReturn("approved")
                    .build();



            Preference preference = preferenceClient.create(preferenceRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("init_point", preference.getInitPoint());
            return ResponseEntity.ok(response);

        } catch (MPApiException e) {
            System.out.println("=== Mercado Pago API Error ===");
            System.out.println("Status code: " + e.getStatusCode());
            System.out.println("Response: " + e.getApiResponse().getContent());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getApiResponse().getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> body) {
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        if (data == null || !data.containsKey("id")) {
            return ResponseEntity.badRequest().body("Sin ID de pago");
        }

        Long paymentId = Long.valueOf(data.get("id").toString());

        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient paymentClient = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = paymentClient.get(paymentId);

            servicePayment.savePaymentFromMP(mpPayment);

            if ("approved".equalsIgnoreCase(mpPayment.getStatus())) {
                String[] parts = mpPayment.getExternalReference().split(":");
                if (parts.length > 1) {
                    String reservationId = parts[1];
                    servicePayment.editReservation(reservationId, "PAYMENT");
                } else {
                    System.err.println("External reference inválido: " + mpPayment.getExternalReference());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error procesando pago");
        }

        return ResponseEntity.ok("OK");
    }


}
