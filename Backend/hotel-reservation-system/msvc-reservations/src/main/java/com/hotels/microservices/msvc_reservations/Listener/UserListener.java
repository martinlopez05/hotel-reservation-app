package com.hotels.microservices.msvc_reservations.Listener;

import com.hotels.microservices.msvc_reservations.config.RabbitUserListenerConfig;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserListener {
    @Autowired
    private IRepositoryReservation repositoryReservation;

    @RabbitListener(queues = RabbitUserListenerConfig.QUEUE)
    public void handleUserDeleted(Long userId) {
        repositoryReservation.deleteAllByUserId(userId);

    }
}
