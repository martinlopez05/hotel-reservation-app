package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.AuthRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;

import java.util.List;

public interface IServiceUser {

    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO create(UserRequestDTO userDTO);
    void delete(Long id);
    UserResponseDTO edit(UserUpdateDTO userUpdateDTO, Long id);

}
