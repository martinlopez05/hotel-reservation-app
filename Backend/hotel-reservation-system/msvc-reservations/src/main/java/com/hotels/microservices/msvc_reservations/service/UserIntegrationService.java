package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.UserDTO;
import com.hotels.microservices.msvc_reservations.exception.ExternalServiceException;
import com.hotels.microservices.msvc_reservations.exception.UserNotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserIntegrationService {

    private final UserClientRest userClientRest;

    @CircuitBreaker(
            name = "userService",
            fallbackMethod = "fallbackUser"
    )
    @Retry(name = "userService")
    public UserDTO getUserData(Long userId) {

        try {
            return userClientRest.getUser(userId).getBody();

        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("The user with ID " + userId + " does not exist.");

        } catch (FeignException e) {
            throw e;
        }
    }

    public UserDTO fallbackUser(Long userId, Exception ex) {
        throw new ExternalServiceException(
                "The User service is currently unavailable."
        );
    }
}
