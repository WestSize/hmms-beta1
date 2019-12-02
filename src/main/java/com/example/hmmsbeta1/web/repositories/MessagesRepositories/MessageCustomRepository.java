package com.example.hmmsbeta1.web.repositories.MessagesRepositories;

import java.util.List;

public interface MessageCustomRepository {
    List findAllByPrivateConversationId(Long id);
    List showOnlyUsersMessages(String email);
}
