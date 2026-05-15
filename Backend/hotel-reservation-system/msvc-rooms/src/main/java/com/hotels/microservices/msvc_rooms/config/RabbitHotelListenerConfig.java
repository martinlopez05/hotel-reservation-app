package com.hotels.microservices.msvc_rooms.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitHotelListenerConfig {
    public static final String EXCHANGE =
            "hotel.exchange";

    public static final String QUEUE =
            "reservation.hotel.deleted.queue";

    public static final String ROUTING_KEY =
            "hotel.deleted.key";

    @Bean
    public Queue hotelQueue() {

        return QueueBuilder
                .durable(QUEUE)
                .build();
    }

    @Bean
    public DirectExchange hotelExchange() {

        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding hotelBinding() {

        return BindingBuilder
                .bind(hotelQueue())
                .to(hotelExchange())
                .with(ROUTING_KEY);
    }
}
