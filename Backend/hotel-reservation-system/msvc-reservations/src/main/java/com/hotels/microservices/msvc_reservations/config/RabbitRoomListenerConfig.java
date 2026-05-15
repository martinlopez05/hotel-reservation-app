package com.hotels.microservices.msvc_reservations.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitRoomListenerConfig {

    public static final String EXCHANGE = "room.exchange";
    public static final String QUEUE = "room.deleted.queue";
    public static final String ROUTING_KEY = "room.deleted.key";

    @Bean
    public Queue roomQueue() {

        return QueueBuilder
                .durable(QUEUE)
                .build();
    }

    @Bean
    public DirectExchange roomExchange() {

        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding roomBinding() {

        return BindingBuilder
                .bind(roomQueue())
                .to(roomExchange())
                .with(ROUTING_KEY);
    }
}
