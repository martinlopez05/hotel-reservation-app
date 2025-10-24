package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.dto.AuthRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.mapper.IUserMapper;
import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceUser implements IServiceUser{

    @Autowired
    IRepositoryUser repositoryUser;

    @Autowired
    IUserMapper userMapper;

    @Override
    public List<UserResponseDTO> findAll() {
        return repositoryUser.findAll().stream().map(userMapper::toUserResponseDTO).toList();
    }

    @Override
    public UserResponseDTO findById(Long id) {
        return repositoryUser.findById(id).map(userMapper::toUserResponseDTO).orElseThrow(
                () -> new EntityNotFoundException("User not found"));
    }


    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = repositoryUser.save(user);
        return userMapper.toUserResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(!repositoryUser.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        repositoryUser.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponseDTO edit(UserUpdateDTO userUpdateDTO, Long id) {
        User editUser = repositoryUser.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userMapper.updateUserFromDTO(userUpdateDTO,editUser);

        User editedUser = repositoryUser.save(editUser);

        return userMapper.toUserResponseDTO(editedUser);

    }


}
