package com.hotels.microservices.msvc_users.mapper;

import com.hotels.microservices.msvc_users.dto.UserRequestDTO;
import com.hotels.microservices.msvc_users.dto.UserResponseDTO;
import com.hotels.microservices.msvc_users.dto.UserUpdateDTO;
import com.hotels.microservices.msvc_users.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserResponseDTO toUserResponseDTO(User user);

    User toEntity(UserRequestDTO userRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    void updateUserFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}
