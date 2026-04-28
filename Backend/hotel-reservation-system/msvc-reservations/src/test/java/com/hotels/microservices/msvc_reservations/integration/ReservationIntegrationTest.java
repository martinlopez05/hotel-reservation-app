package com.hotels.microservices.msvc_reservations.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.RoomClientRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.HotelDTO;
import com.hotels.microservices.msvc_reservations.dto.ReservationRequestDTO;
import com.hotels.microservices.msvc_reservations.dto.RoomDTO;
import com.hotels.microservices.msvc_reservations.dto.UserDTO;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.rabbitmq.listener.simple.auto-startup=false"
})
@AutoConfigureMockMvc
@Testcontainers
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Autowired
    private IRepositoryReservation repositoryReservation;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserClientRest userFeignClient;

    @MockitoBean
    private HotelClientRest hotelFeignClient;

    @MockitoBean
    private RoomClientRest roomFeignClient;

    private static final String url = "/reservations";

    private UserDTO userDTO;
    private RoomDTO roomDTO;
    private HotelDTO hotelDTO;



    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @BeforeEach
    void setUp(){
        repositoryReservation.deleteAll();

        userDTO = UserDTO.builder().id(1L).username("martin").build();
        hotelDTO = HotelDTO.builder().id(1L).name("Hotel Hilton").build();
        roomDTO = RoomDTO.builder().hotelId(1L).id(1L).roomNumber(12).pricePerNight(150.0).rating(3).build();
    }

    @AfterEach
    void cleanUp() {
        repositoryReservation.deleteAll();
    }

    @Nested
    class CreateReservation{

        @Test
        void shouldReturnCreated_whenReservationIscreated() throws Exception {

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(hotelFeignClient.getHotel(1L,false)).willReturn(ResponseEntity.ok(hotelDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));

            ReservationRequestDTO requestDTO = ReservationRequestDTO.builder()
                    .hotelId(1L)
                    .roomId(1L)
                    .userId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();

            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            List<Reservation> reservationsInDB = repositoryReservation.findAll();

            assertEquals(1,reservationsInDB.size());
            assertEquals(1L, reservationsInDB.get(0).getHotelId());
            assertEquals(1L, reservationsInDB.get(0).getRoomId());

        }


        @Test
        void shouldReturnBadRequest_whenReservationRequestInvalid() throws Exception {

            ReservationRequestDTO requestDTO = ReservationRequestDTO.builder()
                    .roomId(1L)
                    .userId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            List<Reservation> reservationsInDB = repositoryReservation.findAll();

            assertEquals(0,reservationsInDB.size());
        }


        @Test
        void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
            given(userFeignClient.getUser(999L))
                    .willThrow(FeignException.NotFound.class);

            ReservationRequestDTO requestDTO = ReservationRequestDTO.builder()
                    .roomId(1L).hotelId(1L).userId(999L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("The user with ID 999 does not exist."))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        void shouldReturnConflict_whenDatesOverlap() throws Exception {
            Reservation reservation = Reservation.builder()
                    .roomId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();

            repositoryReservation.save(reservation);

            ReservationRequestDTO requestDTO = ReservationRequestDTO.builder()
                    .roomId(1L).hotelId(1L).userId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 5))
                    .checkOutDate(LocalDate.of(2026, 5, 12))
                    .build();

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));
            given(hotelFeignClient.getHotel(1L,false)).willReturn(ResponseEntity.ok(hotelDTO));

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Room is reserved on the dates"))
                    .andExpect(jsonPath("$.status").value(409));

        }

    }

    @Nested
    class GetAllReservations{

        @Test
        void shouldReturnOk_whenReservationsExist() throws Exception {

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));

            Reservation reservation = Reservation.builder()
                    .roomId(1L)
                    .id("res-17")
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value("res-17"))
                    .andExpect(jsonPath("$[0].hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$[0].username").value("martin"));

        }

        @Test
        void shouldReturn200AndEmptyList_whenNoReservationsExist() throws Exception {
            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void shouldReturn500_whenExternalServiceIsDown() throws Exception {
            Reservation reservation = Reservation.builder()
                    .roomId(1L).userId(1L).hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);


            given(roomFeignClient.getRoom(1L))
                    .willThrow(FeignException.ServiceUnavailable.class);


            mockMvc.perform(get(url))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(503));
        }
    }

    @Nested
    class GetReservationByUserId {

        @Test
        void shouldReturnOk_whenUserHasReservations() throws Exception {
            Reservation reservation = Reservation.builder()
                    .roomId(1L)
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));

            mockMvc.perform(get(url + "/user/" + 1 ))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].userId").value(1));
        }

        @Test
        void shouldReturnOkAndEmptyList_whenUserHasNoReservations() throws Exception {

            mockMvc.perform(get(url + "/user/"+ 999))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void shouldReturnServiceUnavailable_whenExternalServiceIsDown() throws Exception {

            Reservation reservation = Reservation.builder()
                    .roomId(1L)
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);

            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));
            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(hotelFeignClient.getHotel(1L, false))
                    .willThrow(FeignException.ServiceUnavailable.class);


            mockMvc.perform(get(url + "/user/" + 1))
                    .andExpect(status().isServiceUnavailable()) // 503
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(503));
        }
    }


    @Nested
    class GetReservationById {

        @Test
        void shouldReturnOk_whenReservationExists() throws Exception {
            String reservationId = "res-100";
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .roomId(1L)
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));

            mockMvc.perform(get(url + "/" + reservationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(reservationId))
                    .andExpect(jsonPath("$.userId").value(1L));
        }

        @Test
        void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
            String invalidId = "id-not-exist";

            mockMvc.perform(get(url + "/" + invalidId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        void shouldReturnServiceUnavailable_whenExternalServiceIsDown() throws Exception {
            String reservationId = "res-200";
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .roomId(1L)
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));
            given(roomFeignClient.getRoom(1L))
                    .willThrow(FeignException.ServiceUnavailable.class);

            mockMvc.perform(get(url + "/" + reservationId))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(503));
        }
    }

    @Nested
    class DeleteReservation{

        @Test
        void shouldReturnNoContent_whenReservationExists() throws Exception {

            String reservationId = "res-200";
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .roomId(1L)
                    .userId(1L)
                    .hotelId(1L)
                    .checkInDate(LocalDate.of(2026, 5, 1))
                    .checkOutDate(LocalDate.of(2026, 5, 10))
                    .build();
            repositoryReservation.save(reservation);

            mockMvc.perform(delete(url + "/" + reservationId))
                    .andExpect(status().isNoContent());

            boolean exist = repositoryReservation.existsById(reservationId);
            assertFalse(exist, "The reservation should deleted");

        }

        @Test
        void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
            String invalidId = "id-not-exist";

            mockMvc.perform(delete(url + "/" + invalidId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

    }




}
