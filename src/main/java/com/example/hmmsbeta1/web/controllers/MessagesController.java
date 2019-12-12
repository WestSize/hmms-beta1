package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.dtos.PrivateMessageDto;
import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.PrivateConversationRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private PrivateMessagesService privateMessagesService;

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    private Long replyId = null;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(Model model, Principal principal) {

        if(privateConversationRepository.findAll().size()>0){
            String userEmail = principal.getName();
            if(messageRepository.showOnlyUsersMessages(userEmail).size()>0) {
                model.addAttribute("messages", messageRepository.showOnlyUsersMessages(userEmail));
                model.addAttribute("textmsgs", messageRepository.findAll());
//                model.addAttribute("seen", privateConversationRepository.findAllUsersConversationByEmail(principal.getName()));
                model.addAttribute("seen", privateConversationRepository.findAll());
                return "messages";
            } else {
                //User user = userRepository.findByEmail(principal.getName());
                //user.setUnreadedMessages(0);
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
            privateConversation.setIsRecipientSeen(0);
            //ot tuka pokazva neprochetenite suobshteni
            userRepository.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userRepository.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
//            int userUnreadedMessages=userRepository.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages();
//            userRepository.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userUnreadedMessages+1);
            privateConversation.setIsSenderSeen(1);
            privateMessagesService.saveMessage(privateConversation);
            message.setPrivateConversation(privateConversation);
            message.setUserSender(principal.getName());
//            message.setMessage(passwordEncoder.encode(message.getMessage()));
            messageRepository.save(message);
            return "redirect:/new-message?success";
        }
    }

    @RequestMapping(value = "/read-message", method = RequestMethod.GET)
//    @ResponseBody()
    public String readMessage(long id, Model model, Principal principal){
        if(!principal.getName().equals(privateConversationRepository.getOne(id).getUserSender()) &&
                !principal.getName().equals(privateConversationRepository.getOne(id).getUserRecipient())){
            return "redirect:/messages?noSuchMsg";
        } else {
            PrivateConversation privateConversation = privateConversationRepository.getOne(id);
            if(principal.getName().equals(privateConversation.getUserSender())){
                privateConversation.setIsSenderSeen(1);
                int userUnreadedMessages=userRepository.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userRepository.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages-=1);
                }
                privateMessagesService.saveMessage(privateConversation);
            } else if (principal.getName().equals(privateConversation.getUserRecipient())){
                privateConversation.setIsRecipientSeen(1);
                int userUnreadedMessages=userRepository.findByEmail(principal.getName()).getUnreadedMessages();
                if(userUnreadedMessages!=0) {
                    userRepository.findByEmail(principal.getName()).setUnreadedMessages(userUnreadedMessages -= 1);
                }
                privateMessagesService.saveMessage(privateConversation);
            }
            model.addAttribute("readConversation", privateConversationRepository.getOne(id));
            model.addAttribute("readMessages", messageRepository.findAllByPrivateConversationId(id));
            model.addAttribute("replymsg", new Message());
            replyId = id;
            return "read-message";
        }
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
        privateConversation.setIsSenderSeen(1);
        privateConversation.setIsRecipientSeen(0);
        userRepository.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userRepository.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages()+1);
        int userUnreadedMessages=userRepository.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages();
        messageRepository.save(message);
        int conversationId = Math.toIntExact(message.getPrivateConversation().getId());
        return "redirect:/read-message?id="+conversationId;
    }

    @RequestMapping(value = "/delete-conversation")
    public String deleteConversation(long id){
        User sender = userRepository.findByEmail(privateConversationRepository.getOne(id).getUserSender());
        User recipient = userRepository.findByEmail(privateConversationRepository.getOne(id).getUserRecipient());
        if(sender.getUnreadedMessages()!=0) {
            sender.setUnreadedMessages(sender.getUnreadedMessages()-1);
            userRepository.save(sender);
        }
        if(recipient.getUnreadedMessages()!=0){
            recipient.setUnreadedMessages(recipient.getUnreadedMessages()-1);
            userRepository.save(recipient);
        }

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

    @RequestMapping(value = "/interview")
    public String inviteForInterviewViaPM(Long id, Principal principal) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy',' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setDateAndTime(formatter.format(date));
        privateConversation.setUserSender(principal.getName());
        privateConversation.setUserRecipient(userRepository.getOne(id).getEmail());
        privateConversation.setMessageSubject("Покана за интервю за работа.");
        privateConversation.setIsRecipientSeen(0);
        //ot tuka pokazva neprochetenite suobshteni
        userRepository.findByEmail(privateConversation.getUserRecipient()).setUnreadedMessages(userRepository.findByEmail(privateConversation.getUserRecipient()).getUnreadedMessages() + 1);
        privateConversation.setIsSenderSeen(1);
        privateMessagesService.saveMessage(privateConversation);
        Message message = new Message();
        message.setPrivateConversation(privateConversation);
        message.setUserSender(principal.getName());
        message.setUserRecipient(userRepository.getOne(id).getEmail());
        message.setMessageText(companyRepository.showOneUserCompany(principal.getName()).getCircularLetter());
        message.setDateAndTime(formatter.format(date));
        messageRepository.save(message);
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
