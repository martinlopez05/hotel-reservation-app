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
import com.hotels.microservices.msvc_reservations.model.ReservationState;
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
import org.springframework.test.context.ActiveProfiles;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
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

    private LocalDate checkIn;
    private LocalDate checkOut;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        dynamicPropertyRegistry.add("eureka.client.enabled", () -> false);
        dynamicPropertyRegistry.add("eureka.client.register-with-eureka", () -> false);
        dynamicPropertyRegistry.add("eureka.client.fetch-registry", () -> false);
        dynamicPropertyRegistry.add("spring.cloud.discovery.enabled", () -> false);
        dynamicPropertyRegistry.add("spring.cloud.service-registry.auto-registration.enabled", () -> false);

        dynamicPropertyRegistry.add("spring.rabbitmq.listener.simple.auto-startup", () -> false);
        dynamicPropertyRegistry.add("spring.rabbitmq.listener.direct.auto-startup", () -> false);
    }

    @BeforeEach
    void setUp() {
        repositoryReservation.deleteAll();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("martin")
                .build();

        hotelDTO = HotelDTO.builder()
                .id(1L)
                .name("Hotel Hilton")
                .build();

        roomDTO = RoomDTO.builder()
                .hotelId(1L)
                .id(1L)
                .roomNumber(12)
                .pricePerNight(150.0)
                .rating(3)
                .build();

        checkIn = LocalDate.now().plusDays(10);
        checkOut = checkIn.plusDays(9);
    }

    @AfterEach
    void cleanUp() {
        repositoryReservation.deleteAll();
    }

    private ReservationRequestDTO validRequestDTO(Long userId) {
        return ReservationRequestDTO.builder()
                .hotelId(1L)
                .roomId(1L)
                .userId(userId)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();
    }

    private Reservation reservationWithSnapshot(String id, Long userId) {
        return Reservation.builder()
                .id(id)
                .roomId(1L)
                .userId(userId)
                .hotelId(1L)
                .hotelName("Hotel Hilton")
                .username("martin")
                .roomNumber(12)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();
    }

    @Nested
    class CreateReservation {

        @Test
        void shouldReturnCreated_whenReservationIsCreated() throws Exception {
            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));

            ReservationRequestDTO requestDTO = validRequestDTO(1L);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            List<Reservation> reservationsInDB = repositoryReservation.findAll();

            assertEquals(1, reservationsInDB.size());

            Reservation savedReservation = reservationsInDB.get(0);

            assertEquals(1L, savedReservation.getHotelId());
            assertEquals(1L, savedReservation.getRoomId());
            assertEquals(1L, savedReservation.getUserId());

            assertEquals("Hotel Hilton", savedReservation.getHotelName());
            assertEquals("martin", savedReservation.getUsername());
            assertEquals(12, savedReservation.getRoomNumber());

            assertEquals(1350.0, savedReservation.getPrice(), 0.01);
            assertEquals(ReservationState.PENDING, savedReservation.getState());
        }

        @Test
        void shouldReturnBadRequest_whenReservationRequestInvalid() throws Exception {
            ReservationRequestDTO requestDTO = ReservationRequestDTO.builder()
                    .roomId(1L)
                    .userId(1L)
                    .checkInDate(checkIn)
                    .checkOutDate(checkOut)
                    .build();

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            List<Reservation> reservationsInDB = repositoryReservation.findAll();

            assertEquals(0, reservationsInDB.size());
        }

        @Test
        void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
            FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);

            given(userFeignClient.getUser(999L)).willThrow(feignNotFound);

            ReservationRequestDTO requestDTO = validRequestDTO(999L);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("The user with ID 999 does not exist."))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        void shouldReturnConflict_whenDatesOverlap() throws Exception {
            Reservation existingReservation = Reservation.builder()
                    .roomId(1L)
                    .checkInDate(checkIn)
                    .checkOutDate(checkOut)
                    .build();

            repositoryReservation.save(existingReservation);

            ReservationRequestDTO requestDTO = validRequestDTO(1L);

            given(userFeignClient.getUser(1L)).willReturn(ResponseEntity.ok(userDTO));
            given(hotelFeignClient.getHotel(1L, false)).willReturn(ResponseEntity.ok(hotelDTO));
            given(roomFeignClient.getRoom(1L)).willReturn(ResponseEntity.ok(roomDTO));

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Room is reserved on the dates"))
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    class GetAllReservations {

        @Test
        void shouldReturnOk_whenReservationsExist() throws Exception {
            Reservation reservation = reservationWithSnapshot("res-17", 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value("res-17"))
                    .andExpect(jsonPath("$[0].hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$[0].username").value("martin"))
                    .andExpect(jsonPath("$[0].roomNumber").value(12));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturn200AndEmptyList_whenNoReservationsExist() throws Exception {
            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnOk_whenFetchingReservationsWithoutCallingExternalServices() throws Exception {
            Reservation reservation = reservationWithSnapshot("res-17", 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$[0].username").value("martin"))
                    .andExpect(jsonPath("$[0].roomNumber").value(12));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }
    }

    @Nested
    class GetReservationByUserId {

        @Test
        void shouldReturnOk_whenUserHasReservations() throws Exception {
            Reservation reservation = reservationWithSnapshot("res-user-1", 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url + "/user/" + 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].userId").value(1))
                    .andExpect(jsonPath("$[0].hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$[0].username").value("martin"))
                    .andExpect(jsonPath("$[0].roomNumber").value(12));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnOkAndEmptyList_whenUserHasNoReservations() throws Exception {
            mockMvc.perform(get(url + "/user/" + 999))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnOk_whenFetchingUserReservationsWithoutCallingExternalServices() throws Exception {
            Reservation reservation = reservationWithSnapshot("res-user-2", 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url + "/user/" + 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].userId").value(1))
                    .andExpect(jsonPath("$[0].hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$[0].username").value("martin"))
                    .andExpect(jsonPath("$[0].roomNumber").value(12));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }
    }

    @Nested
    class GetReservationById {

        @Test
        void shouldReturnOk_whenReservationExists() throws Exception {
            String reservationId = "res-100";

            Reservation reservation = reservationWithSnapshot(reservationId, 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(get(url + "/" + reservationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(reservationId))
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$.username").value("martin"))
                    .andExpect(jsonPath("$.roomNumber").value(12));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
            String invalidId = "id-not-exist";

            mockMvc.perform(get(url + "/" + invalidId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }
    }

    @Nested
    class DeleteReservation {

        @Test
        void shouldReturnNoContent_whenReservationExists() throws Exception {
            String reservationId = "res-200";

            Reservation reservation = reservationWithSnapshot(reservationId, 1L);

            repositoryReservation.save(reservation);

            mockMvc.perform(delete(url + "/" + reservationId))
                    .andExpect(status().isNoContent());

            boolean exists = repositoryReservation.existsById(reservationId);

            assertFalse(exists, "The reservation should be deleted");

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
            String invalidId = "id-not-exist";

            mockMvc.perform(delete(url + "/" + invalidId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }
    }

    @Nested
    class UpdateReservationState {

        @Test
        void shouldReturnOk_whenStateIsUpdatedSuccessfully() throws Exception {
            String reservationId = "res-200";

            Reservation reservation = reservationWithSnapshot(reservationId, 1L);
            reservation.setState(ReservationState.PENDING);

            repositoryReservation.save(reservation);

            String newState = "PAYMENT";

            mockMvc.perform(put(url + "/" + reservationId + "/state")
                            .param("state", newState))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state").value("PAYMENT"))
                    .andExpect(jsonPath("$.hotelName").value("Hotel Hilton"))
                    .andExpect(jsonPath("$.username").value("martin"))
                    .andExpect(jsonPath("$.roomNumber").value(12));

            Reservation updatedReservation = repositoryReservation.findById(reservationId).get();

            assertEquals(newState, updatedReservation.getState().name());
            assertEquals("Hotel Hilton", updatedReservation.getHotelName());
            assertEquals("martin", updatedReservation.getUsername());
            assertEquals(12, updatedReservation.getRoomNumber());

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }

        @Test
        void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
            String invalidId = "id-not-exist";

            mockMvc.perform(put(url + "/" + invalidId + "/state")
                            .param("state", "PAYMENT"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));

            verifyNoInteractions(userFeignClient);
            verifyNoInteractions(hotelFeignClient);
            verifyNoInteractions(roomFeignClient);
        }
    }
}