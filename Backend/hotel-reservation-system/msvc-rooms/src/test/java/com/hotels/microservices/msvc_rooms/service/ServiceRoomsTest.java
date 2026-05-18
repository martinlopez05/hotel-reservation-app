package com.hotels.microservices.msvc_rooms.service;

import com.hotels.microservices.msvc_rooms.dto.RoomDTO;
import com.hotels.microservices.msvc_rooms.dto.RoomUpdateDTO;
import com.hotels.microservices.msvc_rooms.exception.RoomNotFoundException;
import com.hotels.microservices.msvc_rooms.mapper.IRoomMapper;
import com.hotels.microservices.msvc_rooms.model.Room;
import com.hotels.microservices.msvc_rooms.repository.IRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRoomsTest {

    @Mock
    private IRoomRepository roomRepository;

    @Mock
    private IRoomMapper roomMapper;

    @InjectMocks
    private ServiceRoom serviceRoom;

    private Room room;
    private RoomDTO roomDTO;
    private RoomUpdateDTO roomUpdateDTO;
    private final Long roomId = 10L;
    private final Long hotelId = 1L;

    @BeforeEach
    void setUp() {
        room = Room.builder()
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
    class FindAllTests {
        @Test
        void findAll_ShouldReturnRoomDTOList() {
            List<Room> rooms = List.of(room);
            when(roomRepository.findAll()).thenReturn(rooms);
            when(roomMapper.toDTO(room)).thenReturn(roomDTO);

            List<RoomDTO> result = serviceRoom.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(roomRepository, times(1)).findAll();
            verify(roomMapper, times(1)).toDTO(room);
        }
    }

    @Nested
    class FindByHotelTests {
        @Test
        void findByHotelId_ShouldReturnRoomDTOList_WhenRoomsExist() {
            List<Room> rooms = List.of(room);
            when(roomRepository.findByHotelId(hotelId)).thenReturn(rooms);
            when(roomMapper.toDTO(room)).thenReturn(roomDTO);

            List<RoomDTO> result = serviceRoom.findbyHotelId(hotelId);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(roomRepository, times(1)).findByHotelId(hotelId);
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_ShouldReturnRoomDTO_WhenExists() {
            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
            when(roomMapper.toDTO(room)).thenReturn(roomDTO);

            RoomDTO result = serviceRoom.findById(roomId);

            assertNotNull(result);
            assertEquals(roomId, result.getId());
            verify(roomRepository, times(1)).findById(roomId);
        }

        @Test
        void findById_ShouldThrowRoomNotFoundException_WhenDoesNotExist() {
            when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> serviceRoom.findById(roomId));
            verify(roomRepository, times(1)).findById(roomId);
            verifyNoInteractions(roomMapper);
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldSaveAndReturnRoomDTO() {
            when(roomMapper.toEntity(roomDTO)).thenReturn(room);
            when(roomRepository.save(room)).thenReturn(room);
            when(roomMapper.toDTO(room)).thenReturn(roomDTO);

            RoomDTO result = serviceRoom.create(roomDTO);

            assertNotNull(result);
            verify(roomRepository, times(1)).save(room);
        }
    }

    @Nested
    class EditTests {
        @Test
        void edit_WhenRoomExists_ShouldUpdateAndReturnRoomDTO() {
            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
            when(roomRepository.save(room)).thenReturn(room);
            when(roomMapper.toDTO(room)).thenReturn(roomDTO);

            RoomDTO result = serviceRoom.editRoom(roomUpdateDTO, roomId);

            assertNotNull(result);
            verify(roomRepository, times(1)).findById(roomId);
            verify(roomMapper, times(1)).updateRoomFromDto(roomUpdateDTO, room);
            verify(roomRepository, times(1)).save(room);
        }

        @Test
        void edit_WhenRoomDoesNotExist_ShouldThrowRoomNotFoundException() {
            when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> serviceRoom.editRoom(roomUpdateDTO, roomId));
            verify(roomRepository, times(1)).findById(roomId);
            verifyNoMoreInteractions(roomRepository);
            verifyNoInteractions(roomMapper);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void deleteByHotelId_ShouldDeleteAllRoomsAssociated() {
            doNothing().when(roomRepository).deleteByHotelId(hotelId);

            serviceRoom.deleteRoomByHotelId(hotelId);

            verify(roomRepository, times(1)).deleteByHotelId(hotelId);
        }
    }
}