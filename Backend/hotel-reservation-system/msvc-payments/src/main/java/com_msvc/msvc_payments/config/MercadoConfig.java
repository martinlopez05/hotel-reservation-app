package com_msvc.msvc_payments.config;
import com.mercadopago.client.preference.PreferenceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mercadopago.MercadoPagoConfig;


@Configuration
public class MercadoConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Bean
    public PreferenceClient preferenceClient() {
        System.out.println("AccessToken cargado: " + accessToken);
        MercadoPagoConfig.setAccessToken(accessToken);
        return new PreferenceClient();
    }
}

