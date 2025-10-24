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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = repositoryUser.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return UserDetailsImpl.builder()
                .user(userEntity)
                .build();

    }
}
