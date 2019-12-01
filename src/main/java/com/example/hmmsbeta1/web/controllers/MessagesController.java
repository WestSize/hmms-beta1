package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.PrivateMessage;
import com.example.hmmsbeta1.web.repositories.PrivateMessagesRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class MessagesController {
    @Autowired
    private PrivateMessagesService privateMessagesService;

    @Autowired
    private PrivateMessagesRepository privateMessagesRepository;


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(Model model) {
        if(privateMessagesRepository.findAll().size()>0){
            model.addAttribute("messages", privateMessagesRepository.findAll());
            return "messages";
        } else {
            return "messages-empty";
        }
}

    @RequestMapping(value = "/new-message", method = RequestMethod.GET)
    public String showNewMessage(Model model){
        model.addAttribute("newmsg", new PrivateMessage());
        return "/new-message";
    }

    @RequestMapping(value="/new-message", method=RequestMethod.POST)
    public String processMessage(@Valid PrivateMessage privateMessage) {

        privateMessagesService.saveMessage(privateMessage);
        return "redirect:/new-message?success";
    }






    @RequestMapping(value = "/read-message", method = RequestMethod.GET)
//    @ResponseBody()
    public String readMessage(long id, Model model){
        model.addAttribute("readMessage", privateMessagesRepository.getOne(id));
        return "read-message";
    }

    @RequestMapping(value = "/delete-message")
    public String deleteMessage(long id){
        privateMessagesRepository.deleteById(id);
        return "redirect:/messages";
    }

}
