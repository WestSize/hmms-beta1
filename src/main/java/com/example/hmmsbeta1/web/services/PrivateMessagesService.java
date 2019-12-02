package com.example.hmmsbeta1.web.services;

import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.PrivateConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateMessagesService {
    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    public void saveMessage(PrivateConversation privateConversation){
        privateConversationRepository.save(privateConversation);
    }

    public List<PrivateConversation> findAll(){
        return privateConversationRepository.findAll();
    }
}
