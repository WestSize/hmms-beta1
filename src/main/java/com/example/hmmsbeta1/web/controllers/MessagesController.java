package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.repositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.PrivateConversationRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class MessagesController {
    @Autowired
    private PrivateMessagesService privateMessagesService;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    private Long replyId = null;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(Model model) {
        if(privateConversationRepository.findAll().size()>0){
            model.addAttribute("messages", privateConversationRepository.findAll());
            model.addAttribute("textmsgs", messageRepository.findAll());
            return "messages";
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
    public String processMessage(@Valid PrivateConversation privateConversation, Message message) {
        privateMessagesService.saveMessage(privateConversation);
        message.setPrivateConversation(privateConversation);
        messageRepository.save(message);
        return "redirect:/new-message?success";
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
    private String replyMessage(Message message){
        PrivateConversation privateConversation = privateConversationRepository.getOne(replyId);
        replyId = null;
        privateConversation.setDateAndTime(message.getDateAndTime());
        message.setPrivateConversation(privateConversation);
        message.setUserRecipient(privateConversation.getUserRecipient());
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
