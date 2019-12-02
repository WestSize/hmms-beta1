package com.example.hmmsbeta1.web.entities;

import com.example.hmmsbeta1.web.entities.Role;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int unreadedMessages;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection < Role > roles;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
//    private List<Worker> workers;

    public User() {}

    public User(String firstName, String lastName, String email, String password, int unreadedMessages) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.unreadedMessages = unreadedMessages;
    }

    public User(String firstName, String lastName, String email, String password, int unreadedMessages, Collection < Role > roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
//        this.unreadedMessages = unreadedMessages;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection < Role > getRoles() {
        return roles;
    }

    public void setRoles(Collection <Role> roles) {
        this.roles = roles;
    }

    public int getUnreadedMessages() {
        return unreadedMessages;
    }

    public void setUnreadedMessages(int unreadedMessages) {
        this.unreadedMessages = unreadedMessages;
    }

    //    public List<Worker> getWorkers() {
//        return workers;
//    }
//
//    public void setWorkers(List<Worker> workers) {
//        this.workers = workers;
//    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + "*********" + '\'' +
                ", roles=" + roles +
                '}';
    }
}