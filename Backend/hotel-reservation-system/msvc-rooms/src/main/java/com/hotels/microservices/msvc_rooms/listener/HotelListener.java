package com.hotels.microservices.msvc_rooms.listener;

import com.hotels.microservices.msvc_rooms.config.RabbitHotelListernerConfig;
import com.hotels.microservices.msvc_rooms.repository.IRepositoryRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class HotelListener {

    @Autowired
    private IRepositoryRoom repositoryRoom;

    @RabbitListener(queues = RabbitHotelListernerConfig.QUEUE)
    public void handleHotelDeleted(Long hotelId) {
        repositoryRoom.findByHotelId(hotelId);
    }
}

