package com_msvc.msvc_payments.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💳 Hotelfly - Motor de Pagos")
                        .version("1.0.0")
                        .description("Pasarela de pagos integrada con Mercado Pago. Controla transacciones, estados de cobro y recepción de Webhooks activos."));
    }
}
