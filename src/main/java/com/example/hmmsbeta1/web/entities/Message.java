package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "messages", schema = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    @JoinColumn(name="conversation_id", nullable = false)
    private PrivateConversation privateConversation;
    @Column
    private String userSender;
    @Column
    private String userRecipient;
    @Lob
    @Column(length=512)
    private String messageText;
    @Column
    private String dateAndTime;


    public Message() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PrivateConversation getPrivateConversation() {
        return privateConversation;
    }

    public void setPrivateConversation(PrivateConversation privateConversation) {
        this.privateConversation = privateConversation;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessage() {
        return messageText;
    }

    public void setMessage(String message) {
        this.messageText = message;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
