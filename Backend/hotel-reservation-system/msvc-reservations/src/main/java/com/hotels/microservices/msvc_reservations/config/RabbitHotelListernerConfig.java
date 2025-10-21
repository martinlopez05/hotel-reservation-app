package com.hotels.microservices.msvc_reservations.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitHotelListernerConfig {
    public static final String EXCHANGE = "hotel.exchange";
    public static final String QUEUE = "reservation.hotel.deleted.queue";
    public static final String ROUTING_KEY = "hotel.deleted.key";

    @Bean
    Queue hotelQueue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    DirectExchange hotelExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Binding hotelBinding(Queue hotelQueue, DirectExchange hotelExchange) {
        return BindingBuilder.bind(hotelQueue).to(hotelExchange).with(ROUTING_KEY);
    }
}
