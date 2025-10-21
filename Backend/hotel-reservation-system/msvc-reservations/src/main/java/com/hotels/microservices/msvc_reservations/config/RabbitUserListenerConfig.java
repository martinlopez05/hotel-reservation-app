package com.hotels.microservices.msvc_reservations.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitUserListenerConfig {
    public static final String EXCHANGE = "user.exchange";
    public static final String QUEUE = "user.deleted.queue";
    public static final String ROUTING_KEY = "user.deleted.key";


    @Bean
    Queue userQueue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    DirectExchange userExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Binding userBinding(Queue userQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(ROUTING_KEY);
    }
}
