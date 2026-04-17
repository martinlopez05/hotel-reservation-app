package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.client.HotelClientRest;
import com.hotels.microservices.msvc_reservations.client.RoomClientRest;
import com.hotels.microservices.msvc_reservations.client.UserClientRest;
import com.hotels.microservices.msvc_reservations.dto.*;
import com.hotels.microservices.msvc_reservations.exception.ReservationNotFoundException;
import com.hotels.microservices.msvc_reservations.exception.RoomIsReservedException;
import com.hotels.microservices.msvc_reservations.exception.RoomNotFoundException;
import com.hotels.microservices.msvc_reservations.mapper.IReservationMapper;
import com.hotels.microservices.msvc_reservations.model.Reservation;
import com.hotels.microservices.msvc_reservations.model.ReservationState;
import com.hotels.microservices.msvc_reservations.repository.IRepositoryReservation;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


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

    private RoomDTO roomDTO;

    private UserDTO userDTO;

    private HotelDTO hotelDTO;

    private ReservationResponseDTO reservationResponseDTO;


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


        roomDTO = RoomDTO.builder()
                .id(reservationRequestDTO.getRoomId())
                .roomNumber(1)
                .pricePerNight(100.00)
                .build();

        hotelDTO = HotelDTO.builder()
                .name("Hilton Buenos Aires")
                .build();

        userDTO = UserDTO.builder()
                .username("martin45630")
                .id(1L)
                .build();

        reservationResponseDTO = ReservationResponseDTO.builder()
                .id("res-123")
                .userId(1L)
                .build();
    }

    @Nested
    class Create {

        @Test
        void  shouldReturnReservationDTO_whenReservationIsCreated(){

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
        void shouldCalculateOneDayPrice_whenCheckInAndCheckOutAreSameDay() {

            reservationRequestDTO.setCheckOutDate(reservationRequestDTO.getCheckInDate());

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    any(Long.class), any(LocalDate.class), any(LocalDate.class))).willReturn(false);

            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));
            given(reservationMapper.toReservation(any(ReservationRequestDTO.class))).willReturn(reservation);
            given(sequenceGeneratorService.generateSequence(anyString())).willReturn(1L);
            given(repositoryReservation.save(any())).willReturn(reservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));

            serviceReservation.create(reservationRequestDTO);

            assertEquals(100.00, reservation.getPrice()); // verifica si realmente se cobro por 1 día solo
        }

        @Test
        void  shouldThrowRoomIsReservedException_whenRoomIsReserved(){

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

    @Nested
    class FindById  {

        @Test
        void shouldReturnReservationResponseDTO_whenReservationExists(){

            String idExist = "res-123";

            given(repositoryReservation.findById(idExist)).willReturn(Optional.of(reservation));
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));
            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));

            ReservationResponseDTO result = serviceReservation.findById(idExist);

            assertEquals(idExist,result.getId());
            assertEquals(roomDTO.getRoomNumber(),result.getRoomNumber());
            assertEquals(hotelDTO.getName(),result.getHotelName());
            verify(repositoryReservation).findById(idExist);


        }

        @Test
        void shouldThrowReservationNotFoundException_whenReservationDoesNotExist() {
            String idNotExist = "res-9999";

            given(repositoryReservation.findById(idNotExist)).willReturn(Optional.empty());

            assertThrows(ReservationNotFoundException.class,()-> {
                serviceReservation.findById(idNotExist);
            });

            verify(reservationMapper,never()).toReservationResponse(any(Reservation.class));

        }

    }

    @Nested
    class FindAll{

        @Test
        void shouldReturnListReservationResponseDTO_whenReservationsExist(){

            List<Reservation> listReservation = List.of(reservation);

            given(repositoryReservation.findAll()).willReturn(listReservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));
            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));

            List<ReservationResponseDTO> result = serviceReservation.findAll();

            assertNotNull(result);
            assertEquals(1,result.size());
            assertEquals(hotelDTO.getName(),result.get(0).getHotelName());
            assertEquals(roomDTO.getRoomNumber(),result.get(0).getRoomNumber());
            assertEquals(userDTO.getUsername(),result.get(0).getUsername());

            verify(repositoryReservation).findAll();

        }


        @Test
        void shouldReturnEmptyList_whenReservationsDoNotExist(){
            given(repositoryReservation.findAll()).willReturn(Collections.emptyList());

            List<ReservationResponseDTO> result = serviceReservation.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reservationMapper, never()).toReservationResponse(any());
        }


    }

    @Nested
    class FindByUserId{

        @Test
        void shouldReturnListReservationResponseDTO_whenReservationExist(){

            List<Reservation> listReservation = List.of(reservation);

            Long idUser = 1L;

            given(repositoryReservation.findByUserId(idUser)).willReturn(listReservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));
            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));

            List<ReservationResponseDTO> result = serviceReservation.findByUserId(idUser);

            assertNotNull(result);
            assertEquals(1,result.size());
            assertEquals(userDTO.getId(),result.get(0).getUserId());
            assertEquals(userDTO.getUsername(),result.get(0).getUsername());
            assertEquals(hotelDTO.getName(),result.get(0).getHotelName());
            assertEquals(roomDTO.getRoomNumber(),result.get(0).getRoomNumber());

            verify(repositoryReservation).findByUserId(idUser);
            verify(reservationMapper).toReservationResponse(reservation);
        }

        @Test
        void shouldReturnEmptyList_whenUserHasNoReservations() {

            Long idUser = 1L;

            given(repositoryReservation.findByUserId(idUser)).willReturn(Collections.emptyList());

            List<ReservationResponseDTO> result = serviceReservation.findByUserId(idUser);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(repositoryReservation).findByUserId(idUser);

            verify(reservationMapper, never()).toReservationResponse(any());
        }
    }

    @Nested
    class DeleteById{

        @Test
        void shouldDelete_whenReservationExist(){

            String idExist = "res-123";

            given(repositoryReservation.existsById(idExist)).willReturn(true);

            serviceReservation.deleteById(idExist);

            verify(repositoryReservation).existsById(idExist);
            verify(repositoryReservation).deleteById(idExist);
        }

        @Test
        void shouldThrowReservationNotFoundException_whenReservationDoesNotExist(){

            String idNotExist = "res-999";

            given(repositoryReservation.existsById(idNotExist)).willReturn(false);

            assertThrows(ReservationNotFoundException.class,()->{
                serviceReservation.deleteById(idNotExist);
            });

            verify(repositoryReservation).existsById(idNotExist);
            verify(repositoryReservation,never()).deleteById(idNotExist);
        }

    }


    @Nested
    class UpdateState{

        @Test
        void shouldReturnReservationResponseDTO_whenReservationExists(){

            String idExist = "res-123";

            given(repositoryReservation.findById(idExist)).willReturn(Optional.of(reservation));
            given(repositoryReservation.save(reservation)).willReturn(reservation);
            given(reservationMapper.toReservationResponse(reservation)).willReturn(reservationResponseDTO);
            given(hotelClientRest.getHotel(anyLong(), eq(false))).willReturn(ResponseEntity.ok(hotelDTO));
            given(userClientRest.getUser(anyLong())).willReturn(ResponseEntity.ok(userDTO));
            given(roomClientRest.getRoom(any(Long.class))).willReturn(ResponseEntity.ok(roomDTO));

            ReservationResponseDTO result = serviceReservation.updateState(idExist,ReservationState.PAYMENT);

            assertNotNull(result);

            assertEquals(ReservationState.PAYMENT, reservation.getState());
            verify(repositoryReservation).findById(idExist);
            verify(repositoryReservation).save(reservation);

        }

        @Test
        void shouldThrowReservationNotFoundException_whenReservationDoesNotExist() {

            String idNotExist = "res-999";

            given(repositoryReservation.findById(idNotExist)).willReturn(Optional.empty());

            assertThrows(ReservationNotFoundException.class, () -> {
                serviceReservation.updateState(idNotExist, ReservationState.PAYMENT);
            });

            verify(repositoryReservation).findById(idNotExist);
            verify(repositoryReservation,never()).save(reservation);

        }

    }

}
