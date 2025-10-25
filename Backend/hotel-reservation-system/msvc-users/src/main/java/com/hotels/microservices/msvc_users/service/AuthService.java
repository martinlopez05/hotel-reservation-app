package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.AuthRequestDTO;
import com.hotels.microservices.msvc_users.dto.AuthResponseDTO;
import com.hotels.microservices.msvc_users.dto.RegisterRequestDTO;
import com.hotels.microservices.msvc_users.dto.RegisterResponseDTO;
import com.hotels.microservices.msvc_users.enums.EnumRoles;
import com.hotels.microservices.msvc_users.model.RoleEntity;
import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryRoleEntity;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import com.hotels.microservices.msvc_users.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    IRepositoryUser repositoryUser;

    @Autowired
    ServiceUserDetailsImpl userDetailService;

    @Autowired
    IRepositoryRoleEntity roleRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO){
        UserDetails userDetails = userDetailService.loadUserByUsername(authRequestDTO.getUsername());

        if(!passwordEncoder.matches(authRequestDTO.getPassword(), userDetails.getPassword())){
            throw new RuntimeException("Incorrect password");
        }

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        String token = jwtUtil.generateToken(userDetails.getUsername(),role);

        User user = repositoryUser.findByUsername(userDetails.getUsername()).orElseThrow( () -> new RuntimeException("User not found"));

        return AuthResponseDTO.builder()
                .username(userDetails.getUsername())
                .role(role)
                .email(user.getEmail())
                .token(token)
                .build();

    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO){
        String username = registerRequestDTO.getUsername();
        String password = registerRequestDTO.getPassword();
        String email = registerRequestDTO.getEmail();
        String role = (registerRequestDTO.getRole() != null && !registerRequestDTO.getRole().isEmpty())
                ? registerRequestDTO.getRole().toUpperCase()
                : "USER";

        if (repositoryUser.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("The email is already registered");
        }


        Optional<RoleEntity> roleOpt = roleRepository.findByName(EnumRoles.valueOf(role));

        if (roleOpt.isEmpty()) {
            throw new RuntimeException("Error: Role " + role + " not found in the database.");
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