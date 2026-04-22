package com.hotels.microservices.msvc_reservations.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationResponseDTO;
import com.hotels.microservices.msvc_reservations.exception.ReservationNotFoundException;
import com.hotels.microservices.msvc_reservations.model.ReservationState;
import com.hotels.microservices.msvc_reservations.service.ServiceReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceReservation serviceReservation;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationRequestDTO reservationRequestDTO;
    private ReservationResponseDTO reservationResponseDTO;
    private List<ReservationResponseDTO> reservationListDTO;
    private String reservationId;
    private Long userId;

    private final String url = "/reservations";

    @BeforeEach
    void setUp() {
        reservationId = "res-123";
        userId = 1L;

        reservationRequestDTO = ReservationRequestDTO.builder()
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(3))
                .hotelId(1L)
                .roomId(1L)
                .userId(userId)
                .build();

        reservationResponseDTO = ReservationResponseDTO.builder()
                .id(reservationId)
                .price(400.00)
                .state(ReservationState.PENDING)
                .userId(userId)
                .orderNumber(1L)
                .checkOutDate(reservationRequestDTO.getCheckOutDate())
                .checkInDate(reservationRequestDTO.getCheckInDate())
                .hotelName("Hilton Buenos Aires")
                .roomNumber(1)
                .username("martin45630")
                .build();

        reservationListDTO = List.of(reservationResponseDTO);

    }

    @Nested
    class GetAllReservations {

        @Test
        void shouldReturn200ok_whenReservationsExist() throws Exception {

            given(serviceReservation.findAll()).willReturn(reservationListDTO);

            mockMvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(reservationListDTO.size()))
                    .andExpect(jsonPath("$[0].id").value(reservationListDTO.get(0).getId()))
                    .andExpect(jsonPath("$[0].username").value(reservationListDTO.get(0).getUsername()))
                    .andExpect(jsonPath("$[0].hotelName").value(reservationListDTO.get(0).getHotelName()))
                    .andExpect(jsonPath("$[0].state").value(reservationListDTO.get(0).getState().toString()));

            verify(serviceReservation).findAll();
        }


        @Test
        void shouldReturn200ok_whenReservationDoesNotExist() throws Exception {
            given(serviceReservation.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(serviceReservation).findAll();
        }

    }

    @Nested
    class GetReservation{

        @Test
        void shouldReturn200ok_whenReservationExists() throws Exception {

            given(serviceReservation.findById(anyString())).willReturn(reservationResponseDTO);

            mockMvc.perform(get(url + "/" + reservationId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(reservationResponseDTO.getId()))
                    .andExpect(jsonPath("$.userId").value(reservationResponseDTO.getUserId()))
                    .andExpect(jsonPath("$.checkInDate").value(reservationResponseDTO.getCheckInDate().toString()))
                    .andExpect(jsonPath("$.checkOutDate").value(reservationResponseDTO.getCheckOutDate().toString()))
                    .andExpect(jsonPath("$.price").value(reservationResponseDTO.getPrice()))
                    .andExpect(jsonPath("$.state").value(reservationResponseDTO.getState().toString()))
                    .andExpect(jsonPath("$.hotelName").value(reservationResponseDTO.getHotelName()))
                    .andExpect(jsonPath("$.username").value(reservationResponseDTO.getUsername()));

            verify(serviceReservation).findById(reservationId);


        }

        @Test
        void shouldReturn404NotFound_whenReservationDoesNotExist() throws Exception {

            String invalidId = "res-999";

            given(serviceReservation.findById(anyString())).willThrow(ReservationNotFoundException.class);

            mockMvc.perform(get(url + "/" + invalidId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(serviceReservation).findById(invalidId);


        }

    }


    @Nested
    class GetReservationByUserId {

        @Test
        void shouldReturn200ok_whenUserHasReservations() throws Exception {
            given(serviceReservation.findByUserId(userId)).willReturn(reservationListDTO);

            mockMvc.perform(get(url + "/user/" + userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(reservationListDTO.size()))
                    .andExpect(jsonPath("$[0].userId").value(userId))
                    .andExpect(jsonPath("$[0].hotelName").value(reservationListDTO.get(0).getHotelName()));
            verify(serviceReservation).findByUserId(userId);
        }

        @Test
        void shouldReturn200ok_whenUserHasNoReservations() throws Exception {

            given(serviceReservation.findByUserId(userId)).willReturn(Collections.emptyList());

            mockMvc.perform(get(url + "/user/" + userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(serviceReservation).findByUserId(userId);
        }
    }

    @Nested
    class CreateReservation{

        @Test
        void shouldReturn201Created_whenReservationIsCreated() throws Exception {
            given(serviceReservation.create(any(ReservationRequestDTO.class))).willReturn(reservationResponseDTO);

            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reservationRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(reservationResponseDTO.getId()))
                    .andExpect(jsonPath("$.username").value(reservationResponseDTO.getUsername()))
                    .andExpect(jsonPath("$.hotelName").value(reservationResponseDTO.getHotelName()))
                    .andExpect(jsonPath("$.roomNumber").value(reservationResponseDTO.getRoomNumber()))
            ;

            verify(serviceReservation).create(any(ReservationRequestDTO.class));

        }

        @Test
        void shouldReturn400BadRequest_whenRequestIsInvalid() throws Exception {

            reservationRequestDTO.setHotelId(null);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reservationRequestDTO)))
                    .andExpect(status().isBadRequest());

            verify(serviceReservation, never()).create(any());
        }


    }


    @Nested
    class DeleteReservation{

        @Test
        void shouldReturn204NoContent_whenReservationIsDeleted() throws Exception {

            mockMvc.perform(delete(url + "/" + reservationId))
                    .andExpect(status().isNoContent());

            verify(serviceReservation).deleteById(reservationId);

        }

        @Test
        void shouldReturn404NotFound_whenReservationToDeleteDoesNotExist() throws Exception {

            String invalidId = "res-999";

            org.mockito.BDDMockito.willThrow(ReservationNotFoundException.class)
                    .given(serviceReservation).deleteById(invalidId);

            mockMvc.perform(delete(url + "/" + invalidId))
                    .andExpect(status().isNotFound());

            verify(serviceReservation).deleteById(invalidId);

        }

    }

    @Nested
    class UpdateState{

        @Test
        void shouldReturn200Ok_whenStateIsUpdated() throws Exception {

            ReservationResponseDTO updatedResponse = ReservationResponseDTO.builder()
                    .id(reservationId)
                    .state(ReservationState.PAYMENT)
                    .build();

            given(serviceReservation.updateState(eq(reservationId), any(ReservationState.class)))
                    .willReturn(updatedResponse);

            mockMvc.perform(put(url + "/" + reservationId + "/state")
                            .param("state", "PAYMENT")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state").value("PAYMENT"));

            verify(serviceReservation).updateState(reservationId, ReservationState.PAYMENT);
        }

        @Test
        void shouldReturn400BadRequest_whenStateIsInvalid() throws Exception {

            mockMvc.perform(put(url + "/" + reservationId + "/state")
                            .param("state", "STATE_INVENTED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
            verify(serviceReservation, never()).updateState(anyString(), any());
        }

    }

}
