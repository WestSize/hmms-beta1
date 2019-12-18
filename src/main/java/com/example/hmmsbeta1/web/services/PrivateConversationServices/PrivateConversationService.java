package com.example.hmmsbeta1.web.services.PrivateConversationServices;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.entities.User;


import java.security.Principal;
import java.util.List;

public interface PrivateConversationService {
    void saveMessage(String userEmail, PrivateConversation privateConversation);
    List<PrivateConversation> findAll();
    PrivateConversation getOne(Long id);
    void deleteById(Long id);
    void setIsSenderSeen(PrivateConversation privateConversation);
    void setIsRecipientSeen(PrivateConversation privateConversation);
    void reply(PrivateConversation privateConversation, Message message, Principal principal);
    void inviteForInterview(PrivateConversation privateConversation,Principal principal, Long id);

}
