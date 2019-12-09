package com.example.hmmsbeta1.web.controllers;


import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Model model, Principal principal){
//        int userUnreededMsgs = userService.countUnreededMessages(principal.getName());
        model.addAttribute("updateuser", new User());
        //        ot tuka pochva reloada na principalite
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); //add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        // do tuka
        return new ModelAndView("home.html");
    }
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String updateUser(Principal principal, User newUserInfo) {
        User oldUserInfo = userRepository.findByEmail(principal.getName());
        oldUserInfo.setFirstName(newUserInfo.getFirstName());
        oldUserInfo.setLastName(newUserInfo.getLastName());
        oldUserInfo.setEmail(newUserInfo.getEmail());
        if(!oldUserInfo.getPassword().equals(newUserInfo.getPassword()) && !newUserInfo.getPassword().equals("")) {
            oldUserInfo.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
        }
        userRepository.save(oldUserInfo);
        return "redirect:/?updateok";
    }
}