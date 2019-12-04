package com.example.hmmsbeta1.web.dtos;

import com.example.hmmsbeta1.web.entities.User;

import javax.validation.constraints.NotEmpty;

public class PrivateMessageDto {

    @NotEmpty
    private String userSender;

    @NotEmpty
    private String userRecipient;

    @NotEmpty
    private String messageText;

    @NotEmpty
    private String dateAndTime;

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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}

