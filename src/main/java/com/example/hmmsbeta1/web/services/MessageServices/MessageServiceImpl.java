package com.example.hmmsbeta1.web.services.MessageServices;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List findAllByPrivateConversationId(Long id) {
        List<Message> messages = messageRepository.findAllByPrivateConversationId(id);
        return messages;
    }

    @Override
    public List showOnlyUsersMessages(String email) {
        List<Message> messages = messageRepository.showOnlyUsersMessages(email);
        return messages;
    }

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    public Message getOne(Long id) {
        Message message = messageRepository.getOne(id);
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = messageRepository.findAll();
        return messages;
    }

    @Override
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }
}
