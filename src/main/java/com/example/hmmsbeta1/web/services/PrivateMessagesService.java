package com.example.hmmsbeta1.web.services;

import com.example.hmmsbeta1.web.entities.PrivateMessage;
import com.example.hmmsbeta1.web.repositories.PrivateMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateMessagesService {
    @Autowired
    private PrivateMessagesRepository privateMessagesRepository;

    public void saveMessage(PrivateMessage privateMessage){
        privateMessagesRepository.save(privateMessage);
    }

    public List<PrivateMessage> findAll(){
        return privateMessagesRepository.findAll();
    }
}
