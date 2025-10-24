package com.hotels.microservices.msvc_users.repository;

import com.hotels.microservices.msvc_users.enums.EnumRoles;
import com.hotels.microservices.msvc_users.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRepositoryRoleEntity extends JpaRepository<RoleEntity,Long> {

    Optional<RoleEntity> findByName(EnumRoles name);
}

