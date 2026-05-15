package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.HotelDTO;
import com.hotels.microservices.msvc_reservations.exception.ExternalServiceException;
import com.hotels.microservices.msvc_reservations.exception.HotelNotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelIntegrationService {

    private final HotelClientRest hotelClientRest;

    @CircuitBreaker(
            name = "hotelService",
            fallbackMethod = "fallbackHotel"
    )
    @Retry(name = "hotelService")
    public  HotelDTO getHotelData(Long hotelId) {
        try {
            return hotelClientRest.getHotel(hotelId,false).getBody();

        } catch (FeignException.NotFound e) {
            throw new HotelNotFoundException("The hotel with ID " + hotelId + " does not exist.");
        } catch (FeignException e) {
            throw e;
        }
    }

    public HotelDTO fallbackHotel(Long hotelId, Exception ex){
        throw new ExternalServiceException("The Hotel service is currently unavailable.");
    }
}
