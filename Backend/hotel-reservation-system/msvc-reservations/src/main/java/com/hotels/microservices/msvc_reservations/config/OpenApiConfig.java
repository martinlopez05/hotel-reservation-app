package com.hotels.microservices.msvc_reservations.config;

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
                        .title("📅 Hotelfly - Servicio de Reservas")
                        .version("1.0.0")
                        .description("Módulo central del negocio. Maneja el flujo de reservas, control de fechas, cálculo de estadías y sincronización asincrónica."));
    }
}
