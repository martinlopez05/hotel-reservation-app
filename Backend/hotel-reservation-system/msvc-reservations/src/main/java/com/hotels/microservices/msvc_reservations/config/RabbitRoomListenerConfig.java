package com.hotels.microservices.msvc_reservations.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitRoomListenerConfig {
    public static final String EXCHANGE = "room.exchange";
    public static final String QUEUE = "room.deleted.queue";
    public static final String ROUTING_KEY = "room.deleted.key";

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
