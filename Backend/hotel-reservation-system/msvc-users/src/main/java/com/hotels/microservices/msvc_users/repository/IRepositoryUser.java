package com.hotels.microservices.msvc_users.repository;


import com.hotels.microservices.msvc_users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Repository
public interface IRepositoryUser extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
