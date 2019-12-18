package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationService;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.MessageServices.MessageService;
import com.example.hmmsbeta1.web.services.PrivateConversationServices.PrivateConversationService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MessagesController {

    @Autowired
    private PrivateConversationService privateConversationService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompanyService companyService;

    private Long replyId = null;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        if(privateConversationService.findAll().size()>0){
            String userEmail = principal.getName();
            if(messageService.showOnlyUsersMessages(userEmail).size()>0) {
                model.addAttribute("messages", messageService.showOnlyUsersMessages(userEmail));
                model.addAttribute("textmsgs", messageService.findAll());
                model.addAttribute("seen", privateConversationService.findAll());
                return "messages";
            } else {
                return "messages-empty";
            }
        } else {
            return "messages-empty";
        }
}

    @RequestMapping(value = "/new-message", method = RequestMethod.GET)
    public String showNewMessage(Model model, Principal principal){
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("newmsg", new PrivateConversation());
        return "/new-message";
    }

    @RequestMapping(value="/new-message", method=RequestMethod.POST)
    public String processMessage(@Valid PrivateConversation privateConversation, Message message, Principal principal) {
        if(userService.findByEmail(message.getUserRecipient())==null){
            return "redirect:/new-message?noSuchUser";
        } else if (message.getUserRecipient().equals(principal.getName())){
            return "redirect:/new-message?noSelfPms";
        } else {
            privateConversationService.saveMessage(principal.getName(), privateConversation);
            messageService.save(message, privateConversation, principal);
            return "redirect:/new-message?success";
        }
    }

    @RequestMapping(value = "/read-message", method = RequestMethod.GET)
    public String readMessage(long id, Model model, Principal principal){
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        if(!principal.getName().equals(privateConversationService.getOne(id).getUserSender()) &&
                !principal.getName().equals(privateConversationService.getOne(id).getUserRecipient())){
            return "redirect:/messages?noSuchMsg";
        } else {
            PrivateConversation privateConversation = privateConversationService.getOne(id);
            if(principal.getName().equals(privateConversation.getUserSender())){
                int userUnreadedMessages=userService.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userService.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages-=1);
                }
                privateConversationService.setIsSenderSeen(privateConversation);
            } else if (principal.getName().equals(privateConversation.getUserRecipient())){
                int userUnreadedMessages=userService.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userService.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages -= 1);
                }
//                privateConversationService.saveMessage(privateConversation);
                privateConversationService.setIsRecipientSeen(privateConversation);
            }
            model.addAttribute("readConversation", privateConversationService.getOne(id));
            model.addAttribute("readMessages", messageService.findAllByPrivateConversationId(id));
            model.addAttribute("replymsg", new Message());
            replyId = id;
            return "read-message";
        }
    }

    @RequestMapping(value = "/read-message", method = RequestMethod.POST)
    private String replyMessage(Message message, Principal principal){
        PrivateConversation privateConversation = privateConversationService.getOne(replyId);
        replyId = null;
        privateConversationService.reply(privateConversation, message, principal);
        messageService.reply(message, privateConversation);
        int conversationId = Math.toIntExact(message.getPrivateConversation().getId());
        return "redirect:/read-message?id="+conversationId;
    }

    @RequestMapping(value = "/delete-conversation")
    public String deleteConversation(long id){
        User sender = userService.findByEmail(privateConversationService.getOne(id).getUserSender());
        User recipient = userService.findByEmail(privateConversationService.getOne(id).getUserRecipient());
        if(sender.getUnreadedMessages()!=0) {
            sender.setUnreadedMessages(sender.getUnreadedMessages()-1);
            userService.update(sender);
        }
        if(recipient.getUnreadedMessages()!=0){
            recipient.setUnreadedMessages(recipient.getUnreadedMessages()-1);
            userService.update(recipient);
        }

        privateConversationService.deleteById(id);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/delete-message")
    public String deleteMessage(long id){
        Message message = messageService.getOne(id);
        int conversationId = Math.toIntExact(message.getPrivateConversation().getId());
        messageService.deleteById(id);
        return "redirect:/read-message?id="+conversationId;
    }

    @RequestMapping(value = "/interview")
    public String inviteForInterviewViaPM(Long id, Principal principal) {
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversationService.inviteForInterview(privateConversation,principal, id);
        messageService.inviteForInterview(privateConversation, principal, id);
        Application application = new Application();
        applicationService.updateToInvited(application, principal, id);
        Long redirectId = companyService.showOneUserCompany(principal.getName()).getId();
        return "redirect:/workers-list?id=" + redirectId;
    }

}
