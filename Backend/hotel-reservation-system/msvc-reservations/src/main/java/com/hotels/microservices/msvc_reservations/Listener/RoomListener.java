package com.hotels.microservices.msvc_reservations.Listener;

import com.hotels.microservices.msvc_reservations.config.RabbitRoomListenerConfig;
import com.hotels.microservices.msvc_reservations.config.RabbitUserListenerConfig;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoomListener {
    @Autowired
    private IRepositoryReservation repositoryReservation;

    @RabbitListener(queues = RabbitRoomListenerConfig.QUEUE)
    public void handleRoomDeleted(Long roomId) {
        System.out.println("Recibido mensaje de habitación eliminada: " + roomId);
        repositoryReservation.deleteAllByRoomId(roomId);
    }
}
