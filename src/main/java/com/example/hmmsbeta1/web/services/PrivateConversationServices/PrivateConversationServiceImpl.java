package com.example.hmmsbeta1.web.services.PrivateConversationServices;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.PrivateConversationRepository;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PrivateConversationServiceImpl implements PrivateConversationService {
    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private UserService userService;

    public void saveMessage(String email, PrivateConversation privateConversation){
        privateConversation.setUserSender(email);
        privateConversation.setIsRecipientSeen(0);
        userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
        privateConversation.setIsSenderSeen(1);
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

    @Override
    public void setIsSenderSeen(PrivateConversation privateConversation) {
        privateConversation.setIsSenderSeen(1);
        privateConversationRepository.save(privateConversation);
    }

    @Override
    public void setIsRecipientSeen(PrivateConversation privateConversation) {
        privateConversation.setIsRecipientSeen(1);
        privateConversationRepository.save(privateConversation);
    }

    @Override
    public void reply(PrivateConversation privateConversation, Message message, Principal principal) {
        privateConversation.setDateAndTime(message.getDateAndTime());
        if(privateConversation.getUserSender().equals(principal.getName())) {
            message.setUserSender(principal.getName());
            message.setUserRecipient(privateConversation.getUserRecipient());
        } else {
            message.setUserSender(principal.getName());
            message.setUserRecipient(privateConversation.getUserSender());
            privateConversation.setUserSender(principal.getName());
            privateConversation.setUserRecipient(message.getUserRecipient());
        }
        privateConversation.setIsSenderSeen(1);
        privateConversation.setIsRecipientSeen(0);
        userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
        //tuka mai lipsva privateConversationRepository.save(privateConversation);
    }

    @Override
    public void inviteForInterview(PrivateConversation privateConversation,Principal principal, Long id) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy',' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        privateConversation.setDateAndTime(formatter.format(date));
        privateConversation.setUserSender(principal.getName());
        privateConversation.setUserRecipient(userService.getOne(id).getEmail());
        privateConversation.setMessageSubject("Покана за интервю за работа.");
        privateConversation.setIsRecipientSeen(0);
        userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages() + 1);
        privateConversation.setIsSenderSeen(1);
        privateConversationRepository.save(privateConversation);

    }
}
