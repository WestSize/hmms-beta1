package com.example.hmmsbeta1.web.repositories;

import com.example.hmmsbeta1.web.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository <User, Long > {
    User findByEmail(String username);
}