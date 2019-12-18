package com.example.hmmsbeta1.web.services.MessageServices;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;


public interface MessageService {
    List findAllByPrivateConversationId(Long id);
    List showOnlyUsersMessages(String email);
    void save(Message message, PrivateConversation privateConversation, Principal principal);
    Message getOne(Long id);
    List<Message> findAll();
    void deleteById(Long id);
    void reply(Message message, PrivateConversation privateConversation);
    void inviteForInterview(PrivateConversation privateConversation, Principal principal, Long id);
}
