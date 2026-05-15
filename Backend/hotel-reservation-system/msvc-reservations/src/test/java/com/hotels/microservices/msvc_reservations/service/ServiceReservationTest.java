package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.dto.*;
import com.hotels.microservices.msvc_reservations.exception.*;
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
    private UserIntegrationService userIntegrationService;
    @Mock
    private HotelIntegrationService hotelIntegrationService;
    @Mock
    private RoomIntegrationService roomIntegrationService;

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
                .roomNumber(1)
                .username("martin45630")
                .hotelName("Hilton Buenos Aires")
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
                .orderNumber(1L)
                .roomNumber(1)
                .username("martin45630")
                .hotelName("Hilton Buenos Aires")
                .state(ReservationState.PENDING)
                .checkInDate(reservationRequestDTO.getCheckInDate())
                .checkOutDate(reservationRequestDTO.getCheckOutDate())
                .price(300.00)
                .build();
    }

    @Nested
    class Create {

        @Test
        void shouldReturnReservationDTO_whenReservationIsCreated() {

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    anyLong(), any(), any())).willReturn(false);

            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);
            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(roomIntegrationService.getRoomData(anyLong())).willReturn(roomDTO);

            given(reservationMapper.toReservation(any(ReservationRequestDTO.class))).willReturn(reservation);
            given(sequenceGeneratorService.generateSequence(anyString())).willReturn(1L);
            given(repositoryReservation.save(any(Reservation.class))).willReturn(reservation);
            given(reservationMapper.toReservationResponse(any(Reservation.class))).willReturn(reservationResponseDTO);

            ReservationResponseDTO result = serviceReservation.create(reservationRequestDTO);

            assertNotNull(result);

            assertEquals("Hilton Buenos Aires", result.getHotelName());
            assertEquals("martin45630", result.getUsername());
            assertEquals(1, result.getRoomNumber());

            assertEquals(300.00, reservation.getPrice());
            assertEquals(ReservationState.PENDING, reservation.getState());

            assertEquals("Hilton Buenos Aires", reservation.getHotelName());
            assertEquals("martin45630", reservation.getUsername());
            assertEquals(1, reservation.getRoomNumber());

            verify(repositoryReservation).save(reservation);
        }

        @Test
        void shouldCalculateOneDayPrice_whenCheckInAndCheckOutAreSameDay() {

            reservationRequestDTO.setCheckOutDate(reservationRequestDTO.getCheckInDate());

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    anyLong(), any(), any())).willReturn(false);

            given(roomIntegrationService.getRoomData(anyLong())).willReturn(roomDTO);
            given(reservationMapper.toReservation(any(ReservationRequestDTO.class))).willReturn(reservation);
            given(sequenceGeneratorService.generateSequence(anyString())).willReturn(1L);
            given(repositoryReservation.save(any())).willReturn(reservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);

            serviceReservation.create(reservationRequestDTO);

            assertEquals(100.00, reservation.getPrice()); // verifica si realmente se cobro por 1 día solo
        }

        @Test
        void shouldThrowRoomIsReservedException_whenRoomIsReserved() {

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan
                    (anyLong(), any(), any())).willReturn(true);

            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);
            given(roomIntegrationService.getRoomData(anyLong())).willReturn(roomDTO);

            assertThrows(RoomIsReservedException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(roomIntegrationService).getRoomData(reservationRequestDTO.getRoomId());
            verify(reservationMapper, never()).toReservation(reservationRequestDTO);

        }

        @Test
        void shouldCalculateOneDayPrice_whenCheckOutDateIsNull() {


            reservationRequestDTO.setCheckOutDate(null);

            given(repositoryReservation.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                    anyLong(), any(), any())).willReturn(false);

            given(roomIntegrationService.getRoomData(anyLong())).willReturn(roomDTO);
            given(reservationMapper.toReservation(any(ReservationRequestDTO.class))).willReturn(reservation);
            given(sequenceGeneratorService.generateSequence(anyString())).willReturn(1L);
            given(repositoryReservation.save(any())).willReturn(reservation);
            given(reservationMapper.toReservationResponse(any())).willReturn(reservationResponseDTO);
            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);

            serviceReservation.create(reservationRequestDTO);


            assertEquals(100.00, reservation.getPrice());
        }

        @Test
        void shouldThrowRoomNotFoundException_whenRoomClientReturns404() {
            given(roomIntegrationService.getRoomData(anyLong())).willThrow(new RoomNotFoundException("Room not found"));
            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);

            assertThrows(RoomNotFoundException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
        }

        @Test
        void shouldThrowExternalServiceException_whenRoomClientIsDown() {
            given(roomIntegrationService.getRoomData(anyLong())).willThrow(new ExternalServiceException("Service down"));
            given(hotelIntegrationService.getHotelData(anyLong())).willReturn(hotelDTO);
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);

            assertThrows(ExternalServiceException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
        }

        @Test
        void shouldThrowExternalServiceException_whenHotelClientIsDown() {
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);
            given(hotelIntegrationService.getHotelData(anyLong())).willThrow(new ExternalServiceException("Service down"));

            assertThrows(ExternalServiceException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
            verify(roomIntegrationService, never()).getRoomData(anyLong());
        }

        @Test
        void shouldThrowExternalServiceException_whenUserClientIsDown() {
            given(userIntegrationService.getUserData(anyLong())).willThrow(new ExternalServiceException("Service down"));

            assertThrows(ExternalServiceException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
        }

        @Test
        void shouldThrowUserNotFoundException_whenUserClientReturns404() {
            given(userIntegrationService.getUserData(anyLong())).willThrow(new UserNotFoundException("User not found"));

            assertThrows(UserNotFoundException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
            verify(hotelIntegrationService, never()).getHotelData(anyLong());
            verify(roomIntegrationService, never()).getRoomData(anyLong());
        }

        @Test
        void shouldThrowHotelNotFoundException_whenHotelClientReturns404() {
            given(userIntegrationService.getUserData(anyLong())).willReturn(userDTO);
            given(hotelIntegrationService.getHotelData(anyLong())).willThrow(new HotelNotFoundException("Hotel not found"));

            assertThrows(HotelNotFoundException.class, () -> {
                serviceReservation.create(reservationRequestDTO);
            });

            verify(repositoryReservation, never()).save(any());
            verify(roomIntegrationService, never()).getRoomData(anyLong());
        }

    }

    @Nested
    class FindById  {

        @Test
        void shouldReturnReservationResponseDTO_whenReservationExists() {

            String idExist = "res-123";

            given(repositoryReservation.findById(idExist)).willReturn(Optional.of(reservation));
            given(reservationMapper.toReservationResponse(reservation)).willReturn(reservationResponseDTO);

            ReservationResponseDTO result = serviceReservation.findById(idExist);

            assertEquals(idExist, result.getId());
            assertEquals(1, result.getRoomNumber());
            assertEquals("Hilton Buenos Aires", result.getHotelName());
            assertEquals("martin45630", result.getUsername());

            verify(repositoryReservation).findById(idExist);
            verify(reservationMapper).toReservationResponse(reservation);

            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
        }

        @Test
        void shouldThrowReservationNotFoundException_whenReservationDoesNotExist() {
            String idNotExist = "res-9999";

            given(repositoryReservation.findById(idNotExist)).willReturn(Optional.empty());

            assertThrows(ReservationNotFoundException.class,()-> {
                serviceReservation.findById(idNotExist);
            });

            verify(reservationMapper,never()).toReservationResponse(any(Reservation.class));
            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
        }

    }

    @Nested
    class FindAll{

        @Test
        void shouldReturnListReservationResponseDTO_whenReservationsExist() {

            List<Reservation> listReservation = List.of(reservation);

            given(repositoryReservation.findAll()).willReturn(listReservation);
            given(reservationMapper.toReservationResponse(reservation)).willReturn(reservationResponseDTO);

            List<ReservationResponseDTO> result = serviceReservation.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Hilton Buenos Aires", result.get(0).getHotelName());
            assertEquals(1, result.get(0).getRoomNumber());
            assertEquals("martin45630", result.get(0).getUsername());

            verify(repositoryReservation).findAll();
            verify(reservationMapper).toReservationResponse(reservation);

            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
        }


        @Test
        void shouldReturnEmptyList_whenReservationsDoNotExist(){
            given(repositoryReservation.findAll()).willReturn(Collections.emptyList());

            List<ReservationResponseDTO> result = serviceReservation.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reservationMapper, never()).toReservationResponse(any());
            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
        }


    }

    @Nested
    class FindByUserId{

        @Test
        void shouldReturnListReservationResponseDTO_whenReservationExist() {

            List<Reservation> listReservation = List.of(reservation);

            Long idUser = 1L;

            given(repositoryReservation.findByUserId(idUser)).willReturn(listReservation);
            given(reservationMapper.toReservationResponse(reservation)).willReturn(reservationResponseDTO);

            List<ReservationResponseDTO> result = serviceReservation.findByUserId(idUser);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(idUser, result.get(0).getUserId());
            assertEquals("martin45630", result.get(0).getUsername());
            assertEquals("Hilton Buenos Aires", result.get(0).getHotelName());
            assertEquals(1, result.get(0).getRoomNumber());

            verify(repositoryReservation).findByUserId(idUser);
            verify(reservationMapper).toReservationResponse(reservation);

            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
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
            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
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
        void shouldReturnReservationResponseDTO_whenReservationExists() {

            String idExist = "res-123";

            ReservationResponseDTO updatedResponseDTO = ReservationResponseDTO.builder()
                    .id("res-123")
                    .userId(1L)
                    .hotelName("Hilton Buenos Aires")
                    .username("martin45630")
                    .roomNumber(1)
                    .state(ReservationState.PAYMENT)
                    .build();

            given(repositoryReservation.findById(idExist)).willReturn(Optional.of(reservation));
            given(repositoryReservation.save(reservation)).willReturn(reservation);
            given(reservationMapper.toReservationResponse(reservation)).willReturn(updatedResponseDTO);

            ReservationResponseDTO result = serviceReservation.updateState(idExist, ReservationState.PAYMENT);

            assertNotNull(result);
            assertEquals(ReservationState.PAYMENT, reservation.getState());
            assertEquals(ReservationState.PAYMENT, result.getState());

            verify(repositoryReservation).findById(idExist);
            verify(repositoryReservation).save(reservation);
            verify(reservationMapper).toReservationResponse(reservation);

            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);
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
            verifyNoInteractions(roomIntegrationService);
            verifyNoInteractions(hotelIntegrationService);
            verifyNoInteractions(userIntegrationService);

        }

    }

}
