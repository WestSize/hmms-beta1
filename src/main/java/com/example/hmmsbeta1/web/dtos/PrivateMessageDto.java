package com.example.hmmsbeta1.web.dtos;

import com.example.hmmsbeta1.web.entities.User;

public class PrivateMessageDto {

    private String messageSubject;

    private String userSender;

    private User userRecipient;

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

    public User getUserRecipient() {
        return userRecipient;
    }

    public void setUserRecipient(User userRecipient) {
        this.userRecipient = userRecipient;
    }
}
