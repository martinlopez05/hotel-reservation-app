package com.hotels.microservices.msvc_reservations.Listener;

import com.hotels.microservices.msvc_reservations.config.RabbitHotelListernerConfig;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotelListener {

    @Autowired
    private IRepositoryReservation reservationRepository;

    @RabbitListener(queues = RabbitHotelListernerConfig.QUEUE)
    public void handleHotelDeleted(Long hotelId) {
        reservationRepository.deleteAllByHotelId(hotelId);
    }
}
