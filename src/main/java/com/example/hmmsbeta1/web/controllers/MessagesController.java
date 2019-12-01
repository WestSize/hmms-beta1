package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.PrivateConversationRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MessagesController {
    @Autowired
    private PrivateMessagesService privateMessagesService;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;


    private Long replyId = null;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(Model model, Principal principal) {
        if(privateConversationRepository.findAll().size()>0){
            String userEmail = principal.getName();
            if(messageRepository.showOnlyUsersMessages(userEmail).size()>0) {
                model.addAttribute("messages", messageRepository.showOnlyUsersMessages(userEmail));
                model.addAttribute("textmsgs", messageRepository.findAll());
                return "messages";
            } else {
                return "messages-empty";
            }
        } else {
            return "messages-empty";
        }
}

    @RequestMapping(value = "/new-message", method = RequestMethod.GET)
    public String showNewMessage(Model model){
        model.addAttribute("newmsg", new PrivateConversation());
        return "/new-message";
    }

    @RequestMapping(value="/new-message", method=RequestMethod.POST)
    public String processMessage(@Valid PrivateConversation privateConversation, Message message, Principal principal) {
        if(userRepository.findByEmail(message.getUserRecipient())==null){
            return "redirect:/new-message?noSuchUser";
        } else if (message.getUserRecipient().equals(principal.getName())){
            return "redirect:/new-message?noSelfPms";
        } else {

            privateConversation.setUserSender(principal.getName());
            privateMessagesService.saveMessage(privateConversation);
            message.setPrivateConversation(privateConversation);
            message.setUserSender(principal.getName());
            messageRepository.save(message);
            return "redirect:/new-message?success";
        }
    }

    @RequestMapping(value = "/read-message", method = RequestMethod.GET)
//    @ResponseBody()
    public String readMessage(long id, Model model){
        model.addAttribute("readConversation", privateConversationRepository.getOne(id));
        model.addAttribute("readMessages", messageRepository.findAllByPrivateConversationId(id));
        model.addAttribute("replymsg", new Message());
        replyId = id;
        return "read-message";
    }

    @RequestMapping(value = "/read-message", method = RequestMethod.POST)
    private String replyMessage(Message message, Principal principal){
        PrivateConversation privateConversation = privateConversationRepository.getOne(replyId);
        replyId = null;
        privateConversation.setDateAndTime(message.getDateAndTime());
        message.setPrivateConversation(privateConversation);
        if(privateConversation.getUserSender().equals(principal.getName())) {
            message.setUserSender(principal.getName());
            message.setUserRecipient(privateConversation.getUserRecipient());
        } else {
            message.setUserSender(principal.getName());
            message.setUserRecipient(privateConversation.getUserSender());
            privateConversation.setUserSender(principal.getName());
            privateConversation.setUserRecipient(message.getUserRecipient());
        }
        messageRepository.save(message);
        return "redirect:/messages?sended";
    }

    @RequestMapping(value = "/delete-conversation")
    public String deleteConversation(long id){
        privateConversationRepository.deleteById(id);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/delete-message")
    public String deleteMessage(long id){
        Message message = messageRepository.getOne(id);
        int conversationId = Math.toIntExact(message.getPrivateConversation().getId());
        messageRepository.deleteById(id);
        return "redirect:/read-message?id="+conversationId;
    }
}
