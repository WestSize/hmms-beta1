//package com.example.hmmsbeta1.web.services;
//
//import com.example.hmmsbeta1.web.entities.User;
//import com.example.hmmsbeta1.web.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MyUserDetailsService implements UserDetailsService {
//
//    private UserRepository userRepository;
//
//    public MyUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public MyUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        User user = this.userRepository.findByEmail(s);
//        UserPrincipal userPrincipal = new UserPrincipal(user);
//        return userPrincipal;
//    }
//}