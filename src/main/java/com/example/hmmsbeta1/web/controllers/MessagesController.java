package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.MessageServices.MessageService;
import com.example.hmmsbeta1.web.services.PrivateConversationServices.PrivateConversationService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class MessagesController {

    @Autowired
    private PrivateConversationService privateConversationService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private CompanyRepository companyRepository;

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
            privateConversation.setUserSender(principal.getName());
            privateConversation.setIsRecipientSeen(0);
            //ot tuka pokazva neprochetenite suobshteni
            userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
            privateConversation.setIsSenderSeen(1);
            privateConversationService.saveMessage(privateConversation);
            message.setPrivateConversation(privateConversation);
            message.setUserSender(principal.getName());
            messageService.save(message);
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
                privateConversation.setIsSenderSeen(1);
                int userUnreadedMessages=userService.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userService.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages-=1);
                }
                privateConversationService.saveMessage(privateConversation);
            } else if (principal.getName().equals(privateConversation.getUserRecipient())){
                privateConversation.setIsRecipientSeen(1);
                int userUnreadedMessages=userService.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userService.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages -= 1);
                }
                privateConversationService.saveMessage(privateConversation);
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
        privateConversation.setIsSenderSeen(1);
        privateConversation.setIsRecipientSeen(0);
        userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
        int userUnreadedMessages=userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages();
        messageService.save(message);
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy',' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setDateAndTime(formatter.format(date));
        privateConversation.setUserSender(principal.getName());
        privateConversation.setUserRecipient(userService.getOne(id).getEmail());
        privateConversation.setMessageSubject("Покана за интервю за работа.");
        privateConversation.setIsRecipientSeen(0);
        //ot tuka pokazva neprochetenite suobshteni
        userService.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userService.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages() + 1);
        privateConversation.setIsSenderSeen(1);
        privateConversationService.saveMessage(privateConversation);
        Message message = new Message();
        message.setPrivateConversation(privateConversation);
        message.setUserSender(principal.getName());
        message.setUserRecipient(userService.getOne(id).getEmail());
        message.setMessageText(companyRepository.showOneUserCompany(principal.getName()).getCircularLetter());
        message.setDateAndTime(formatter.format(date));
        messageService.save(message);
        List <Application> applications = applicationRepository.showApplicationByCandidateId(id);
        Application application = new Application();
        for (int i = 0; i < applications.size(); i++) {
            Application nowApp = applications.get(i);
            if(nowApp.getCompanyId().equals(companyRepository.showOneUserCompany(principal.getName()).getId())){
                application = nowApp;
            }
        }
        application.setInvited(true);
        applicationRepository.save(application);
        Long redirectId = companyRepository.showOneUserCompany(principal.getName()).getId();
        return "redirect:/workers-list?id=" + redirectId;
    }

}
