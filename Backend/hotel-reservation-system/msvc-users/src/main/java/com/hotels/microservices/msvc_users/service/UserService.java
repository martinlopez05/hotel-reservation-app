package com.hotels.microservices.msvc_users.service;


import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.exception.UserNotFoundException;
import com.hotels.microservices.msvc_users.mapper.IUserMapper;
import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IRepositoryUser repositoryUser;

    private final IUserMapper userMapper;

    @Override
    public List<UserResponseDTO> findAll() {
        return repositoryUser.findAll().stream().map(userMapper::toUserResponseDTO).toList();
    }

    @Override
    public UserResponseDTO findById(Long id) {
        return repositoryUser.findById(id).map(userMapper::toUserResponseDTO).orElseThrow(
                () -> new UserNotFoundException("User with id " + id +" not found"));
    }


    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = repositoryUser.save(user);
        return userMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public void delete(Long id) {
        if(!repositoryUser.existsById(id)) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }
        repositoryUser.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponseDTO edit(UserUpdateDTO userUpdateDTO, Long id) {
        User editUser = repositoryUser.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id +" not found"));
        userMapper.updateUserFromDTO(userUpdateDTO,editUser);

        User editedUser = repositoryUser.save(editUser);

        return userMapper.toUserResponseDTO(editedUser);

    }
}
