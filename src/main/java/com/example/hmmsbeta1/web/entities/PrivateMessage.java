package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "PrivateMessages")
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "messageSubject", nullable = false)
    private String messageSubject;
    @Column(nullable = false)
    private String userSender;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "recipient_id")
//    private User userRecipient;
    @Column(nullable = false)
    private String userRecipient;
    @Column
    private String messageText;
    @Column
    private String date;

    public PrivateMessage() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
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
}
