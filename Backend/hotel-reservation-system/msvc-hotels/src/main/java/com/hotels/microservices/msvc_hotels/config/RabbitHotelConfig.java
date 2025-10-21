package com.hotels.microservices.msvc_hotels.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitHotelConfig {

    public static final String EXCHANGE = "hotel.exchange";
    public static final String QUEUE = "hotel.deleted.queue";
    public static final String ROUTING_KEY = "hotel.deleted.key";

    @Bean
    Queue queue() {
        return new Queue(QUEUE, false); // false: no durable
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}

