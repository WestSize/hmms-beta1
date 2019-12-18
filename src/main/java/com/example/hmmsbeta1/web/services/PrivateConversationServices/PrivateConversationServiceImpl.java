package com.example.hmmsbeta1.web.services.PrivateConversationServices;

import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.PrivateConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateConversationServiceImpl implements PrivateConversationService {
    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    public void saveMessage(PrivateConversation privateConversation){
        privateConversationRepository.save(privateConversation);
    }

    public List<PrivateConversation> findAll(){
        return privateConversationRepository.findAll();
    }

    @Override
    public PrivateConversation getOne(Long id) {
        PrivateConversation privateConversation = privateConversationRepository.getOne(id);
        return privateConversation;
    }

    @Override
    public void deleteById(Long id) {
        privateConversationRepository.deleteById(id);
    }
}
