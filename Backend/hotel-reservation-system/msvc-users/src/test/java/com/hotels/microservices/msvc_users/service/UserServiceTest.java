package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.exception.UserNotFoundException;
import com.hotels.microservices.msvc_users.mapper.IUserMapper;
import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
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
class UserServiceTest {

    @Mock
    private IRepositoryUser repositoryUser;

    @Mock
    private IUserMapper userMapper;

    @InjectMocks
    private UserService serviceUser;

    private User user;
    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;
    private UserUpdateDTO userUpdateDTO;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .username("martin_dev")
                .email("martin@mail.com")
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(userId)
                .username("martin_dev")
                .email("martin@mail.com")
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .username("martin_dev")
                .email("martin@mail.com")
                .build();

        userUpdateDTO = UserUpdateDTO.builder()
                .username("martin_updated")
                .build();
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_ShouldReturnUserResponseDTOList() {
            when(repositoryUser.findAll()).thenReturn(List.of(user));
            when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

            List<UserResponseDTO> result = serviceUser.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(repositoryUser, times(1)).findAll();
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_WhenUserExists_ShouldReturnUserResponseDTO() {
            when(repositoryUser.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

            UserResponseDTO result = serviceUser.findById(userId);

            assertNotNull(result);
            assertEquals("martin_dev", result.getUsername());
        }

        @Test
        void findById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
            when(repositoryUser.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> serviceUser.findById(userId));
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_ShouldSaveAndReturnUserResponseDTO() {
            when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
            when(repositoryUser.save(user)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

            UserResponseDTO result = serviceUser.create(userRequestDTO);

            assertNotNull(result);
            verify(repositoryUser, times(1)).save(user);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_WhenUserExists_ShouldDeleteUser() {
            when(repositoryUser.existsById(userId)).thenReturn(true);

            serviceUser.delete(userId);

            verify(repositoryUser, times(1)).deleteById(userId);
        }

        @Test
        void delete_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
            when(repositoryUser.existsById(userId)).thenReturn(false);

            assertThrows(UserNotFoundException.class, () -> serviceUser.delete(userId));
            verify(repositoryUser, never()).deleteById(anyLong());
        }
    }

    @Nested
    class EditTests {
        @Test
        void edit_WhenUserExists_ShouldUpdateAndReturnUserResponseDTO() {
            when(repositoryUser.findById(userId)).thenReturn(Optional.of(user));
            when(repositoryUser.save(user)).thenReturn(user);
            when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

            UserResponseDTO result = serviceUser.edit(userUpdateDTO, userId);

            assertNotNull(result);
            verify(userMapper, times(1)).updateUserFromDTO(userUpdateDTO, user);
            verify(repositoryUser, times(1)).save(user);
        }
    }
}
