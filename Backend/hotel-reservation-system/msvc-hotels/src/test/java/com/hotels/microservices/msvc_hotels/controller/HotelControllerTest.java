package com.hotels.microservices.msvc_hotels.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import com.hotels.microservices.msvc_hotels.exception.ExternalServiceException;
import com.hotels.microservices.msvc_hotels.exception.HotelNotFoundException;
import com.hotels.microservices.msvc_hotels.service.IServiceHotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IServiceHotel serviceHotel;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private HotelDTO hotelDTO;
    private Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        RoomDTO roomDTO = new RoomDTO(10L, 101, 1L, 2, "room_url", true, 4, 120.0, "Deluxe Room");
        List<RoomDTO> rooms = new ArrayList<>();
        rooms.add(roomDTO);

        hotelDTO = new HotelDTO(
                hotelId,
                "Hotel Test",
                "Av. Siempre Viva 742",
                "Argentina",
                "Buenos Aires",
                "Hermoso hotel de prueba",
                "hotel_url",
                4,
                "test@hotel.com",
                "+5411223344",
                rooms
        );
    }

    @Nested
    class GetAllHotelTests {
        @Test
        void getAllHotel_ShouldReturnListOfHotels() throws Exception {
            List<HotelDTO> hotels = List.of(hotelDTO);
            when(serviceHotel.findAll()).thenReturn(hotels);

            mockMvc.perform(get("/hotels"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Hotel Test"));

            verify(serviceHotel, times(1)).findAll();
        }
    }

    @Nested
    class GetHotelTests {
        @Test
        void getHotel_ShouldReturnHotel_WhenExists() throws Exception {
            when(serviceHotel.findById(hotelId, false)).thenReturn(hotelDTO);

            mockMvc.perform(get("/hotels/{id}", hotelId)
                            .param("includeRooms", "false"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(hotelId));

            verify(serviceHotel, times(1)).findById(hotelId, false);
        }

        @Test
        void getHotel_ShouldReturn404_WhenDoesNotExist() throws Exception {
            when(serviceHotel.findById(hotelId, false)).thenThrow(new HotelNotFoundException("Hotel not found"));

            mockMvc.perform(get("/hotels/{id}", hotelId)
                            .param("includeRooms", "false"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Hotel not found"));

            verify(serviceHotel, times(1)).findById(hotelId, false);
        }

        @Test
        void getHotel_ShouldReturn503_WhenExternalServiceFails() throws Exception {
            when(serviceHotel.findById(hotelId, true)).thenThrow(new ExternalServiceException("Room service is temporarily unavailable"));

            mockMvc.perform(get("/hotels/{id}", hotelId)
                            .param("includeRooms", "true"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.message").value("Room service is temporarily unavailable"));

            verify(serviceHotel, times(1)).findById(hotelId, true);
        }
    }

    @Nested
    class CreateHotelTests {
        @Test
        void createHotel_ShouldReturn201AndCreatedHotel() throws Exception {
            when(serviceHotel.create(any(HotelDTO.class))).thenReturn(hotelDTO);

            mockMvc.perform(post("/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(hotelDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Hotel Test"));

            verify(serviceHotel, times(1)).create(any(HotelDTO.class));
        }
    }

    @Nested
    class DeleteHotelTests {
        @Test
        void deleteHotel_ShouldReturn204AndSendRabbitMessage_WhenExists() throws Exception {
            // Nota: Al ser un método void que no lanza excepción en este flujo, podés omitir el doNothing()
            mockMvc.perform(delete("/hotels/{id}", hotelId))
                    .andExpect(status().isNoContent());

            verify(serviceHotel, times(1)).delete(hotelId);
            verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), eq(hotelId));
        }

        @Test
        void deleteHotel_ShouldReturn404_WhenDoesNotExist() throws Exception {
            // Sintaxis correcta para mockear excepciones en métodos void
            doThrow(new HotelNotFoundException("Hotel not found")).when(serviceHotel).delete(hotelId);

            mockMvc.perform(delete("/hotels/{id}", hotelId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Hotel not found"));

            verify(serviceHotel, times(1)).delete(hotelId);
            verifyNoInteractions(rabbitTemplate);
        }
    }

    @Nested
    class EditHotelTests {
        @Test
        void editHotel_ShouldReturn200AndEditedHotel() throws Exception {
            when(serviceHotel.edit(any(HotelDTO.class), eq(hotelId))).thenReturn(hotelDTO);

            mockMvc.perform(put("/hotels/{id}", hotelId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(hotelDTO)))
                    .andExpect(status().isOk());

            verify(serviceHotel, times(1)).edit(any(HotelDTO.class), eq(hotelId));
        }
    }
}