package com.example.hmmsbeta1.web.services.MessageServices;

import com.example.hmmsbeta1.web.entities.Message;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MessageService {
    List findAllByPrivateConversationId(Long id);
    List showOnlyUsersMessages(String email);
    void save(Message message);
    Message getOne(Long id);
    List<Message> findAll();
    void deleteById(Long id);
}
