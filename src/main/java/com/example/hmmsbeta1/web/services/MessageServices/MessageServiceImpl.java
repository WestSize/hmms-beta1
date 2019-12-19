package com.example.hmmsbeta1.web.services.MessageServices;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService, CompanyService companyService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.companyService = companyService;
    }

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
    public void save(Message message, PrivateConversation privateConversation, Principal principal) {
        message.setPrivateConversation(privateConversation);
        message.setUserSender(principal.getName());
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

    @Override
    public void reply(Message message, PrivateConversation privateConversation) {
        message.setPrivateConversation(privateConversation);
        messageRepository.save(message);
    }

    @Override
    public void inviteForInterview(PrivateConversation privateConversation, Principal principal, Long id) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy',' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        Message message = new Message();
        message.setPrivateConversation(privateConversation);
        message.setUserSender(principal.getName());
        message.setUserRecipient(userService.getOne(id).getEmail());
        message.setMessageText(companyService.showOneUserCompany(principal.getName()).getCircularLetter());
        message.setDateAndTime(formatter.format(date));
        messageRepository.save(message);
    }
}
