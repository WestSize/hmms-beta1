package com.example.hmmsbeta1.web.services;

import com.example.hmmsbeta1.web.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.user.getRoles().forEach(p->{GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+p);
        authorities.add(authority);
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFirstName() {
        return this.user.getFirstName();
    }

    public String getLastName() { return this.user.getLastName();}

    public int getUnreadedMessages(){
        return this.user.getUnreadedMessages();
    }

    public String getUserWorkingStatus(){
        return this.user.getWorkingStatus();
    }

    public String getEmail(){
        return this.user.getEmail();
    }

    public Long getId(){
        return this.user.getId();
    }

    public String getAvatarPath(){
        return this.user.getAvatarPath();
    }

    public int getAge(){
        return this.user.getAge();
    }

    public String getFacebookPage(){
        return this.user.getFacebookPage();
    }

    public String getDescription(){
        return this.user.getDescription();
    }

}
