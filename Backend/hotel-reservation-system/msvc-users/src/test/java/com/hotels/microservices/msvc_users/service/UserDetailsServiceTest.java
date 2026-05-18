package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private IRepositoryUser repositoryUser;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;
    private final String usernameOrEmail = "martin_dev";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("martin_dev")
                .email("martin@mail.com")
                .password("encoded_password")
                .build();
    }

    @Nested
    class LoadUserByUsernameTests {

        @Test
        void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
            when(repositoryUser.findByUsernameOrEmail(usernameOrEmail)).thenReturn(Optional.of(user));

            UserDetails result = userDetailsService.loadUserByUsername(usernameOrEmail);

            assertNotNull(result);


            verify(repositoryUser, times(1)).findByUsernameOrEmail(usernameOrEmail);
        }

        @Test
        void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {

            when(repositoryUser.findByUsernameOrEmail(usernameOrEmail)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () ->
                    userDetailsService.loadUserByUsername(usernameOrEmail)
            );

            verify(repositoryUser, times(1)).findByUsernameOrEmail(usernameOrEmail);
        }
    }
}
