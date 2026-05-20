package com.hotels.microservices.msvc_rooms.config;

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
                        .title("🛏️ Hotelfly - Servicio de Habitaciones")
                        .version("1.0.0")
                        .description("Gestión del inventario de habitaciones, definición de tipos (Simple, Suite, Doble) y control dinámico de disponibilidad."));
    }
}
