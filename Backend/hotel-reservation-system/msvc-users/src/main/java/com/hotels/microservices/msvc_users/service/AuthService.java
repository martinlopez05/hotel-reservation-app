package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IRepositoryUser repositoryUser;

    private final UserDetailsServiceImpl userDetailService;

    private final IRepositoryRoleEntity roleRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;


    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        UserDetails userDetails = userDetailService.loadUserByUsername(authRequestDTO.getUsername());

        if (!passwordEncoder.matches(authRequestDTO.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        User user = repositoryUser.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username" + userDetails.getUsername()  + " not found"));

        return AuthResponseDTO.builder()
                .id(user.getId())
                .username(userDetails.getUsername())
                .role(role)
                .email(user.getEmail())
                .token(token)
                .build();
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        String username = registerRequestDTO.getUsername();
        String password = registerRequestDTO.getPassword();
        String email = registerRequestDTO.getEmail();
        String role = (registerRequestDTO.getRole() != null && !registerRequestDTO.getRole().isEmpty())
                ? registerRequestDTO.getRole().toUpperCase()
                : "USER";

        if (repositoryUser.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("The email is already registered");
        }


        Optional<RoleEntity> roleOpt = roleRepository.findByName(EnumRoles.valueOf(role));

        if (roleOpt.isEmpty()) {
            throw new RoleNotFoundException("Error: Role " + role + " not found in the database.");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roleEntity(roleOpt.get())
                .build();

        repositoryUser.save(user);

        return RegisterResponseDTO.builder()
                .message("User registered successfully!")
                .build();


    }
}