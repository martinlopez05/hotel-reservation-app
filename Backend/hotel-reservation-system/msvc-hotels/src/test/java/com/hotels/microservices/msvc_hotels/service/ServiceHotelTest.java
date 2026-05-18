package com.hotels.microservices.msvc_hotels.service;


import com.hotels.microservices.msvc_hotels.client.RoomClientRest;
import com.hotels.microservices.msvc_hotels.dtos.HotelDTO;
import com.hotels.microservices.msvc_hotels.dtos.RoomDTO;
import com.hotels.microservices.msvc_hotels.exception.ExternalServiceException;
import com.hotels.microservices.msvc_hotels.exception.HotelNotFoundException;
import com.hotels.microservices.msvc_hotels.mapper.IHotelMapper;
import com.hotels.microservices.msvc_hotels.model.Hotel;
import com.hotels.microservices.msvc_hotels.repository.IRepositoryHotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceHotelTest {

    @Mock
    private IRepositoryHotel repositoryHotel;

    @Mock
    private IHotelMapper hotelMapper;

    @Mock
    private RoomClientRest roomClientRest;

    @InjectMocks
    private ServiceHotel serviceHotel;

    private Hotel hotel;
    private HotelDTO hotelDTO;
    private final Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotelDTO = new HotelDTO();
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_ShouldReturnHotelDTOList() {
            List<Hotel> hotels = List.of(hotel);
            when(repositoryHotel.findAll()).thenReturn(hotels);
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);

            List<HotelDTO> result = serviceHotel.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(repositoryHotel, times(1)).findAll();
            verify(hotelMapper, times(1)).toDTO(hotel);
        }


    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_WithoutRooms_ShouldReturnHotelDTO() {
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);

            HotelDTO result = serviceHotel.findById(hotelId, false);

            assertNotNull(result);
            verify(repositoryHotel, times(1)).findById(hotelId);
            verifyNoInteractions(roomClientRest);
        }

        @Test
        void findById_WithRooms_ShouldReturnHotelDTOWithRooms() {
            List<RoomDTO> rooms = List.of(new RoomDTO());
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);
            when(roomClientRest.getRoomsByHotel(hotelId)).thenReturn(ResponseEntity.of(Optional.of(rooms)));

            HotelDTO result = serviceHotel.findById(hotelId, true);

            assertNotNull(result);
            verify(repositoryHotel, times(1)).findById(hotelId);
            verify(roomClientRest, times(1)).getRoomsByHotel(hotelId);
        }

        @Test
        void findById_HotelNotFound_ShouldThrowException() {
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> serviceHotel.findById(hotelId, false));
            verify(repositoryHotel, times(1)).findById(hotelId);
            verifyNoInteractions(hotelMapper);
        }

        @Test
        void findById_WhenExternalServiceFails_ShouldThrowExternalServiceException() {
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);

            when(roomClientRest.getRoomsByHotel(hotelId)).thenThrow(new RuntimeException("Connection refused"));

            assertThrows(ExternalServiceException.class, () -> serviceHotel.findById(hotelId, true));

            verify(repositoryHotel, times(1)).findById(hotelId);
            verify(roomClientRest, times(1)).getRoomsByHotel(hotelId);
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldSaveAndReturnHotelDTO() {
            when(hotelMapper.toHotel(hotelDTO)).thenReturn(hotel);
            when(repositoryHotel.save(hotel)).thenReturn(hotel);
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);

            HotelDTO result = serviceHotel.create(hotelDTO);

            assertNotNull(result);
            verify(repositoryHotel, times(1)).save(hotel);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_WhenHotelExists_ShouldDeleteHotel() {
            when(repositoryHotel.existsById(hotelId)).thenReturn(true);

            serviceHotel.delete(hotelId);

            verify(repositoryHotel, times(1)).existsById(hotelId);
            verify(repositoryHotel, times(1)).deleteById(hotelId);
        }

        @Test
        void delete_WhenHotelDoesNotExist_ShouldThrowHotelNotFoundException() {
            when(repositoryHotel.existsById(hotelId)).thenReturn(false);

            assertThrows(HotelNotFoundException.class, () -> serviceHotel.delete(hotelId));

            verify(repositoryHotel, times(1)).existsById(hotelId);
            verifyNoMoreInteractions(repositoryHotel);
        }
    }

    @Nested
    class EditTests {
        @Test
        void edit_WhenHotelExists_ShouldUpdateAndReturnHotelDTO() {
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(repositoryHotel.save(hotel)).thenReturn(hotel);
            when(hotelMapper.toDTO(hotel)).thenReturn(hotelDTO);

            HotelDTO result = serviceHotel.edit(hotelDTO, hotelId);

            assertNotNull(result);
            verify(repositoryHotel, times(1)).findById(hotelId);
            verify(hotelMapper, times(1)).updateHotelFromDTO(hotelDTO, hotel);
            verify(repositoryHotel, times(1)).save(hotel);
        }

        @Test
        void edit_WhenHotelDoesNotExist_ShouldThrowException() {
            when(repositoryHotel.findById(hotelId)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> serviceHotel.edit(hotelDTO, hotelId));
            verify(repositoryHotel, times(1)).findById(hotelId);
            verifyNoMoreInteractions(repositoryHotel);
            verifyNoInteractions(hotelMapper);
        }
    }
}