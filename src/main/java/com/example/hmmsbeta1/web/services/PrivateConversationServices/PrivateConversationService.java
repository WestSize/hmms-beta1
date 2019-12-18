package com.example.hmmsbeta1.web.services.PrivateConversationServices;

import com.example.hmmsbeta1.web.entities.PrivateConversation;


import java.util.List;

public interface PrivateConversationService {
    void saveMessage(PrivateConversation privateConversation);
    List<PrivateConversation> findAll();
    PrivateConversation getOne(Long id);
    void deleteById(Long id);

}
