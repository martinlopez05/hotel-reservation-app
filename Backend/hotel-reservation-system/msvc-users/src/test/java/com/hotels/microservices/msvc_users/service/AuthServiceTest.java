package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.AuthRequestDTO;
import com.hotels.microservices.msvc_users.dto.AuthResponseDTO;
import com.hotels.microservices.msvc_users.dto.RegisterRequestDTO;
import com.hotels.microservices.msvc_users.dto.RegisterResponseDTO;
import com.hotels.microservices.msvc_users.enums.EnumRoles;
import com.hotels.microservices.msvc_users.exception.BadCredentialsException;
import com.hotels.microservices.msvc_users.exception.EmailAlreadyExistsException;
import com.hotels.microservices.msvc_users.exception.RoleNotFoundException;
import com.hotels.microservices.msvc_users.exception.UserNotFoundException;
import com.hotels.microservices.msvc_users.model.RoleEntity;
import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryRoleEntity;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IRepositoryUser repositoryUser;

    @Mock
    private UserDetailsServiceImpl userDetailService;

    @Mock
    private IRepositoryRoleEntity roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RoleEntity roleEntity;
    private UserDetails userDetails;

    private AuthRequestDTO authRequestDTO;
    private RegisterRequestDTO registerRequestDTO;

    private final String username = "martin_dev";
    private final String password = "password123";
    private final String encodedPassword = "encoded_password123";
    private final String email = "martin@mail.com";

    @BeforeEach
    void setUp() {
        roleEntity = new RoleEntity();

        user = User.builder()
                .id(1L)
                .username(username)
                .email(email)
                .password(encodedPassword)
                .roleEntity(roleEntity)
                .build();

        authRequestDTO = AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        String roleName = "USER";
        registerRequestDTO = RegisterRequestDTO.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(roleName)
                .build();

        userDetails = mock(UserDetails.class);
    }

    @Nested
    class LoginTests {

        @BeforeEach
        void setupLoginMocks() {
            lenient().when(userDetailService.loadUserByUsername(username)).thenReturn(userDetails);
            lenient().when(userDetails.getUsername()).thenReturn(username);
            lenient().when(userDetails.getPassword()).thenReturn(encodedPassword);
        }

        @Test
        void login_ShouldReturnAuthResponseDTO_WhenCredentialsAreValid() {
            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails).getAuthorities();

            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            when(jwtUtil.generateToken(username, "ROLE_USER")).thenReturn("mocked-jwt-token");
            when(repositoryUser.findByUsername(username)).thenReturn(Optional.of(user));

            AuthResponseDTO response = authService.login(authRequestDTO);

            assertNotNull(response);
            assertEquals("mocked-jwt-token", response.getToken());
            assertEquals(username, response.getUsername());
            assertEquals(email, response.getEmail());

            verify(userDetailService, times(1)).loadUserByUsername(username);
            verify(passwordEncoder, times(1)).matches(password, encodedPassword);
            verify(jwtUtil, times(1)).generateToken(username, "ROLE_USER");
        }

        @Test
        void login_ShouldThrowBadCredentialsException_WhenPasswordDoesNotMatch() {
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.login(authRequestDTO));

            verify(jwtUtil, never()).generateToken(anyString(), anyString());
            verify(repositoryUser, never()).findByUsername(anyString());
        }

        @Test
        void login_ShouldThrowUserNotFoundException_WhenUserDoesNotExistInDatabase() {
            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails).getAuthorities();
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            when(jwtUtil.generateToken(username, "ROLE_USER")).thenReturn("mocked-jwt-token");

            when(repositoryUser.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> authService.login(authRequestDTO));
        }
    }

    @Nested
    class RegisterTests {

        @Test
        void register_ShouldReturnSuccessMessage_WhenDataIsValid() {
            when(repositoryUser.findByEmail(email)).thenReturn(Optional.empty());

            when(roleRepository.findByName(EnumRoles.USER)).thenReturn(Optional.of(roleEntity));
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

            RegisterResponseDTO response = authService.register(registerRequestDTO);

            assertNotNull(response);
            assertEquals("User registered successfully!", response.getMessage());

            verify(repositoryUser, times(1)).save(any(User.class));
        }

        @Test
        void register_ShouldThrowEmailAlreadyExistsException_WhenEmailIsDuplicate() {
            when(repositoryUser.findByEmail(email)).thenReturn(Optional.of(user));

            assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequestDTO));

            verify(roleRepository, never()).findByName(any());
            verify(repositoryUser, never()).save(any(User.class));
        }

        @Test
        void register_ShouldThrowRoleNotFoundException_WhenRoleDoesNotExistInDB() {
            when(repositoryUser.findByEmail(email)).thenReturn(Optional.empty());

            when(roleRepository.findByName(EnumRoles.USER)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> authService.register(registerRequestDTO));

            verify(passwordEncoder, never()).encode(anyString());
            verify(repositoryUser, never()).save(any(User.class));
        }
    }
}
