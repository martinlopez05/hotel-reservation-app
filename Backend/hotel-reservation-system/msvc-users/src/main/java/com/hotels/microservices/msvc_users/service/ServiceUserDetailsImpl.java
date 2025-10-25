package com.hotels.microservices.msvc_users.service;

import com.hotels.microservices.msvc_users.model.User;
import com.hotels.microservices.msvc_users.model.UserDetailsImpl;
import com.hotels.microservices.msvc_users.repository.IRepositoryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServiceUserDetailsImpl implements UserDetailsService {
    @Autowired
    IRepositoryUser repositoryUser;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User userEntity = repositoryUser.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return UserDetailsImpl.builder()
                .user(userEntity)
                .build();

    }
}
