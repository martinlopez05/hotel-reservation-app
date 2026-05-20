package com.microservicios.msvc_reviews.config;

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
                        .title("⭐️ Hotelfly - Sistema de Reseñas")
                        .version("1.0.0")
                        .description("Endpoints destinados a las calificaciones, comentarios y feedback de los usuarios sobre sus experiencias en los hoteles."));
    }
}
