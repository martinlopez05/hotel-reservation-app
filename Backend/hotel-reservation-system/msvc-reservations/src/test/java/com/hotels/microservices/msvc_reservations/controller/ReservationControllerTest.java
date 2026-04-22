package com.hotels.microservices.msvc_reservations.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
        void shouldReturnListReservationResponseDTO_whenReservationsExist() throws Exception {

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
        void shouldReturnListEmpty_whenReservationDoesNotExist() throws Exception {
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
        void shouldReturnReservationResponseDTO_whenReservationExists() throws Exception {

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
        void shouldThrowReservationNotFoundException_whenReservationDoesNotExist() throws Exception {

            String reservationIdInexist = "res-999";

            given(serviceReservation.findById(anyString())).willThrow(ReservationNotFoundException.class);

            mockMvc.perform(get(url + "/" + reservationIdInexist)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(serviceReservation).findById(reservationIdInexist);


        }

    }


    @Nested
    class GetReservationByUserId {

        @Test
        void shouldReturnListReservationDTO_whenUserHasReservations() throws Exception {
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
        void shouldReturnEmptyList_whenUserHasNoReservations() throws Exception {

            given(serviceReservation.findByUserId(userId)).willReturn(Collections.emptyList());

            mockMvc.perform(get(url + "/user/" + userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(serviceReservation).findByUserId(userId);
        }
    }

}
