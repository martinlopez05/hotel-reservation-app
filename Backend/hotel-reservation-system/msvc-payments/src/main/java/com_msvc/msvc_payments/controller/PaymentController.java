package com_msvc.msvc_payments.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import com_msvc.msvc_payments.dto.PaymentRequestDTO;
import com_msvc.msvc_payments.dto.PaymentResponseDTO;
import com_msvc.msvc_payments.exception.ExternalServiceException;
import com_msvc.msvc_payments.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Endpoints para la gestión de pagos, integración nativa con Mercado Pago y procesamiento de Webhooks")
public class PaymentController {

    private final PreferenceClient preferenceClient;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${hotelfly.api.webhook-url}")
    private String webhookUrl;

    @Value("${hotelfly.frontend.url}")
    private String frontendUrl;

    private final IPaymentService servicePayment;


    @GetMapping
    @Operation(
            summary = "Listar todos los registros de pagos",
            description = "Retorna el historial completo de transacciones económicas registradas en el sistema."
    )
    public ResponseEntity<List<PaymentResponseDTO>> getPayments() {
        return ResponseEntity.ok(servicePayment.findAll());
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(
            summary = "Buscar pago por ID de Reserva",
            description = "Obtiene los detalles del pago asociado a una reserva específica."
    )
    public ResponseEntity<PaymentResponseDTO> getByReservation(
            @Parameter(description = "ID alfanumérico de la reserva vinculada", example = "res-9b1deb4d-3b7d")
            @PathVariable String reservationId) {
        return ResponseEntity.ok(servicePayment.findByReservation(reservationId));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un pago manual",
            description = "Permite dar de alta un pago de forma interna en el sistema (ej: efectivo o transferencia directa) y actualiza el estado de la reserva en cascada."
    )
    public ResponseEntity<PaymentResponseDTO> createManualPayment(@RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO paymentResponseDTO = servicePayment.saveManualPayment(dto);
        servicePayment.editReservation(paymentResponseDTO.getReservationId(), "PAYMENT");
        return ResponseEntity.ok(paymentResponseDTO);
    }


    @PostMapping("/mercadopago")
    @Operation(
            summary = "Crear preferencia de Mercado Pago",
            description = "Genera un checkout en la API de Mercado Pago con el monto de la reserva, las URL de retorno para el frontend y la URL de notificación (Webhook). Retorna la 'init_point' para redirigir al usuario."
    )
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
                    .notificationUrl(webhookUrl)
                    .backUrls(
                            PreferenceBackUrlsRequest.builder()
                                    .success(frontendUrl)
                                    .failure(frontendUrl)
                                    .pending(frontendUrl)
                                    .build()
                    )
                    .autoReturn("approved")
                    .build();

            Preference preference = preferenceClient.create(preferenceRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("init_point", preference.getInitPoint());
            return ResponseEntity.ok(response);

        } catch (MPApiException e) {
            throw new ExternalServiceException("Mercado Pago API Error: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            throw new ExternalServiceException("Error creating Mercado Pago preference: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    @Operation(
            summary = "Recibir notificaciones asincrónicas (Webhook)",
            description = "Endpoint expuesto para que Mercado Pago envíe notificaciones en tiempo real sobre los cambios de estado de los pagos. Valida la transacción con la pasarela externa y actualiza la reserva si el estado es APPROVED."
    )
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> body) {
        Map<String, Object> data = (Map<String, Object>) body.get("data");

        if (data == null || !data.containsKey("id")) {
            throw new IllegalArgumentException("Invalid webhook payload: Missing payment data ID");
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
                    throw new IllegalArgumentException("Invalid external reference format in Mercado Pago payment: " + mpPayment.getExternalReference());
                }
            }
        } catch (Exception e) {
            throw new ExternalServiceException("Error processing Mercado Pago webhook for payment ID " + paymentId + ". Details: " + e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }
}
