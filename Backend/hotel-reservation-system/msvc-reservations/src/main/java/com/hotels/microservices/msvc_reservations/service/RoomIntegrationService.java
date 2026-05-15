package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.RoomClientRest;
import com.hotels.microservices.msvc_reservations.dto.HotelDTO;
import com.hotels.microservices.msvc_reservations.dto.RoomDTO;
import com.hotels.microservices.msvc_reservations.exception.ExternalServiceException;
import com.hotels.microservices.msvc_reservations.exception.RoomNotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomIntegrationService {

    private final RoomClientRest roomClientRest;

    @CircuitBreaker(
            name = "roomService",
            fallbackMethod = "fallbackRoom"
    )
    @Retry(name = "roomService")
    public RoomDTO getRoomData(Long roomId) {
        try {
            return roomClientRest.getRoom(roomId).getBody();

        } catch (FeignException.NotFound e) {
            throw new RoomNotFoundException("The room with ID " + roomId + " does not exist.");
        }  catch (FeignException e) {
            throw e;
        }
    }

    public RoomDTO fallbackRoom(Long roomId, Exception ex){
        throw new ExternalServiceException("The Room service is currently unavailable.");
    }
}
