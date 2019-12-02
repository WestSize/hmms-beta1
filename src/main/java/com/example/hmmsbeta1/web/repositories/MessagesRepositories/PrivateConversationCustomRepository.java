package com.example.hmmsbeta1.web.repositories.MessagesRepositories;

import com.example.hmmsbeta1.web.entities.PrivateConversation;

import java.util.List;

public interface PrivateConversationCustomRepository {
    List<PrivateConversation> findAllUsersConversationByEmail(String email);
}
