package com.hotels.microservices.msvc_users.config;

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
                        .title("🔐 Hotelfly - Core de Usuarios y Autenticación")
                        .version("1.0.0")
                        .description("API encargada del registro, inicio de sesión, generación de tokens JWT y gestión de perfiles de usuario."));
    }
}