package com.hotels.microservices.msvc_rooms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.exception.RoomNotFoundException;
import com.hotels.microservices.msvc_rooms.service.IServiceRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IServiceRoom serviceRoom;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private RoomDTO roomDTO;
    private RoomUpdateDTO roomUpdateDTO;
    private final Long roomId = 10L;
    private final Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        roomDTO = RoomDTO.builder()
                .id(roomId)
                .roomNumber(101)
                .hotelId(hotelId)
                .capacity(2)
                .imageUrl("room_url")
                .available(true)
                .rating(4)
                .pricePerNight(120.0)
                .description("Deluxe Room")
                .build();

        roomUpdateDTO = RoomUpdateDTO.builder()
                .roomNumber(102)
                .capacity(3)
                .imageUrl("new_url")
                .available(true)
                .rating(5)
                .pricePerNight(150.0)
                .description("Updated Room")
                .build();
    }

    @Nested
    class GetAllRoomsTests {
        @Test
        void getAllRooms_ShouldReturnListOfRooms() throws Exception {
            List<RoomDTO> rooms = List.of(roomDTO);
            when(serviceRoom.findAll()).thenReturn(rooms);

            mockMvc.perform(get("/rooms"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].roomNumber").value(101));

            verify(serviceRoom, times(1)).findAll();
        }
    }

    @Nested
    class GetRoomTests {
        @Test
        void getRoom_ShouldReturnRoom_WhenExists() throws Exception {
            when(serviceRoom.findById(roomId)).thenReturn(roomDTO);

            mockMvc.perform(get("/rooms/{id}", roomId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(roomId))
                    .andExpect(jsonPath("$.description").value("Deluxe Room"));

            verify(serviceRoom, times(1)).findById(roomId);
        }

        @Test
        void getRoom_ShouldReturn404_WhenDoesNotExist() throws Exception {
            when(serviceRoom.findById(roomId)).thenThrow(new RoomNotFoundException("Room not found"));

            mockMvc.perform(get("/rooms/{id}", roomId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Room not found"));

            verify(serviceRoom, times(1)).findById(roomId);
        }
    }

    @Nested
    class GetRoomsByHotelTests {
        @Test
        void getRoomsByHotel_ShouldReturnRoomsList() throws Exception {
            List<RoomDTO> rooms = List.of(roomDTO);
            when(serviceRoom.findbyHotelId(hotelId)).thenReturn(rooms);

            mockMvc.perform(get("/rooms/hotel/{hotelId}", hotelId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].hotelId").value(hotelId));

            verify(serviceRoom, times(1)).findbyHotelId(hotelId);
        }
    }

    @Nested
    class CreateRoomTests {
        @Test
        void createRoom_ShouldReturn201AndCreatedRoom() throws Exception {
            when(serviceRoom.create(any(RoomDTO.class))).thenReturn(roomDTO);

            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(roomDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.roomNumber").value(101));

            verify(serviceRoom, times(1)).create(any(RoomDTO.class));
        }
    }

    @Nested
    class DeleteRoomTests {
        @Test
        void deleteRoom_ShouldReturn204AndSendRabbitMessage_WhenExists() throws Exception {
            // Como deleteRoom es void y representa el éxito, omitimos doNothing()
            mockMvc.perform(delete("/rooms/{id}", roomId))
                    .andExpect(status().isNoContent());

            verify(serviceRoom, times(1)).deleteRoom(roomId);
            verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), eq(roomId));
        }

        @Test
        void deleteRoom_ShouldReturn404_WhenDoesNotExist() throws Exception {
            doThrow(new RoomNotFoundException("Room not found")).when(serviceRoom).deleteRoom(roomId);

            mockMvc.perform(delete("/rooms/{id}", roomId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Room not found"));

            verify(serviceRoom, times(1)).deleteRoom(roomId);
            verifyNoInteractions(rabbitTemplate);
        }
    }

    @Nested
    class DeleteRoomsByHotelTests {
        @Test
        void deleteRoomsByHotel_ShouldReturn204_WhenSuccessful() throws Exception {
            mockMvc.perform(delete("/rooms/hotel/{hotelId}", hotelId))
                    .andExpect(status().isNoContent());

            verify(serviceRoom, times(1)).deleteRoomByHotelId(hotelId);
        }
    }

    @Nested
    class EditRoomTests {
        @Test
        void editRoom_ShouldReturn200AndEditedRoom() throws Exception {
            when(serviceRoom.editRoom(any(RoomUpdateDTO.class), eq(roomId))).thenReturn(roomDTO);

            mockMvc.perform(put("/rooms/{id}", roomId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(roomUpdateDTO)))
                    .andExpect(status().isOk());

            verify(serviceRoom, times(1)).editRoom(any(RoomUpdateDTO.class), eq(roomId));
        }
    }
}
