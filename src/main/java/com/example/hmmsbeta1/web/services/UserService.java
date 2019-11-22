package com.example.hmmsbeta1.web.services;

import com.example.hmmsbeta1.web.dtos.UserRegistrationDto;
import com.example.hmmsbeta1.web.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration);
}