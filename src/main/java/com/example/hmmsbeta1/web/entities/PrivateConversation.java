package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PrivateConversation")
public class PrivateConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "messageSubject", nullable = false)
    private String messageSubject;
    @Column(nullable = false)
    private String userSender;
    @Column(nullable = false)
    private String userRecipient;
    @OneToMany(mappedBy="privateConversation", cascade = CascadeType.ALL)
    private Set<Message> messages;
    @Column
    private String dateAndTime;
    @Column
    private int isSenderSeen;
    @Column
    private int isRecipientSeen;

    public PrivateConversation() {
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }

    public String getUserRecipient() {
        return userRecipient;
    }

    public void setUserRecipient(String userRecipient) {
        this.userRecipient = userRecipient;
    }

    public int getIsSenderSeen() {
        return isSenderSeen;
    }

    public void setIsSenderSeen(int isSenderSeen) {
        this.isSenderSeen = isSenderSeen;
    }

    public int getIsRecipientSeen() {
        return isRecipientSeen;
    }

    public void setIsRecipientSeen(int isRecipientSeen) {
        this.isRecipientSeen = isRecipientSeen;
    }
}
