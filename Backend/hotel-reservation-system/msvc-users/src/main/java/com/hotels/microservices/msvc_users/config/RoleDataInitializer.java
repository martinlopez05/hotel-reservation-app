package com.hotels.microservices.msvc_users.config;

import com.hotels.microservices.msvc_users.enums.EnumRoles;
import com.hotels.microservices.msvc_users.model.RoleEntity;
import com.hotels.microservices.msvc_users.repository.IRepositoryRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    @Autowired
    private IRepositoryRoleEntity roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(RoleEntity.builder().name(EnumRoles.ADMIN).build());
            roleRepository.save(RoleEntity.builder().name(EnumRoles.USER).build());
            System.out.println("Roles insert");
        }
    }
}