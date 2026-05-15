package com.hotels.microservices.msvc_reservations.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitUserListenerConfig {
    public static final String EXCHANGE = "user.exchange";
    public static final String QUEUE = "user.deleted.queue";
    public static final String ROUTING_KEY = "user.deleted.key";

    @Bean
    public Queue userQueue() {

        return QueueBuilder
                .durable(QUEUE)
                .build();
    }

    @Bean
    public DirectExchange userExchange() {

        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding userBinding() {

        return BindingBuilder
                .bind(userQueue())
                .to(userExchange())
                .with(ROUTING_KEY);
    }
}
