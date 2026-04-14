package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.RoomClientRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.*;
import com.hotels.microservices.msvc_reservations.exception.RoomIsReservedException;
import com.hotels.microservices.msvc_reservations.mapper.IReservationMapper;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.model.ReservationState;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ServiceReservationTest {


    @Mock
    private IRepositoryReservation repositoryReservation;

    @Mock
    private HotelClientRest hotelClientRest;

    @Mock
    private RoomClientRest roomClientRest;

    @Mock
    private UserClientRest userClientRest;

    @Mock
    private IReservationMapper reservationMapper;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @InjectMocks
    private ServiceReservation serviceReservation;


    private ReservationRequestDTO reservationRequestDTO;

    private Reservation reservation;


    @BeforeEach
    void setUp(){

        reservationRequestDTO = ReservationRequestDTO.builder()
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(3))
                .hotelId(1L)
                .roomId(1L)
                .userId(1L)
                .build();


        reservation = Reservation.builder()
                .price(400.00)
                .state(ReservationState.PENDING)
                .roomId(reservationRequestDTO.getRoomId())
                .hotelId(reservationRequestDTO.getHotelId())
                .userId(reservationRequestDTO.getUserId())
                .orderNumber(1L)
                .checkOutDate(reservationRequestDTO.getCheckOutDate())
                .checkInDate(reservationRequestDTO.getCheckInDate())
                .build();
    }

    @Nested
    class create {

        @Test
        void  create_shouldReturnReservationDTO_whenReservationIsCreated(){

            RoomDTO roomDTO = RoomDTO.builder()
                    .id(reservationRequestDTO.getRoomId())
                    .roomNumber(1)
                    .pricePerNight(100.00)
                    .build();

            HotelDTO hotelDTO = HotelDTO.builder()
                    .name("Hilton Buenos Aires")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .username("martin45630")
                    .build();

            ReservationResponseDTO reservationResponseDTO = ReservationResponseDTO.builder()
                    .id("res-123")
                    .build();

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan
                    (any(Long.class),any(LocalDate.class),any(LocalDate.class))).willReturn(false);

            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));

            given(reservationMapper.toReservation(any(ReservationRequestDTO.class))).willReturn(reservation);

            given(sequenceGeneratorService.generateSequence(anyString())).willReturn(1L);
            given(repositoryReservation.save(any())).willReturn(reservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);

            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));


            ReservationResponseDTO result = serviceReservation.create(reservationRequestDTO);


            assertNotNull(result);

            assertEquals("Hilton Buenos Aires", result.getHotelName());
            assertEquals("martin45630", result.getUsername());
            assertEquals(1, result.getRoomNumber());
            assertEquals(300.00, reservation.getPrice());
            assertEquals(ReservationState.PENDING, reservation.getState());

        }

        @Test
        void  create_shouldThrowsRoomIsReservatedException_whenRoomIsReservated() throws RoomIsReservedException {

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan
                    (any(Long.class),any(LocalDate.class),any(LocalDate.class))).willReturn(true);


            assertThrows(RoomIsReservedException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(roomClientRest,never()).getRoom(reservationRequestDTO.getRoomId());
            verify(reservationMapper,never()).toReservation(reservationRequestDTO);
            verify(roomClientRest,never()).getRoom(reservationRequestDTO.getRoomId());

        }

    }

}
