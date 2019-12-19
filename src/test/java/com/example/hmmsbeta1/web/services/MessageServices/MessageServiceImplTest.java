package com.example.hmmsbeta1.web.services.MessageServices;

import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationServiceImpl;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceImplTest {
    MessageRepository messageRepository;

    UserService userService;

    CompanyService companyService;

    @BeforeEach
    private void setupTest(){
        messageRepository = Mockito.mock(MessageRepository.class);
        userService = Mockito.mock(UserService.class);
        companyService = Mockito.mock(CompanyService.class);
    }

    @Test
    void findAllByPrivateConversationId() {
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setId((long)1);
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Message message = new Message();
            message.setPrivateConversation(privateConversation);
            messages.add(message);
        }
        Mockito.when(messageRepository.findAllByPrivateConversationId((long)1)).thenReturn(messages);
        assertEquals(messageRepository.findAllByPrivateConversationId((long)1), messages);
    }

    @Test
    void showOnlyUsersMessages() {
        User user = new User();
        user.setId((long)1);
        user.setEmail("gosho");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Message message = new Message();
            message.setUserSender(user.getEmail());
            message.setUserRecipient(user.getEmail());
            messages.add(message);
        }
        Mockito.when(messageRepository.showOnlyUsersMessages("gosho")).thenReturn(messages);
        assertEquals(messageRepository.showOnlyUsersMessages("gosho"), messages);

    }

    @Test
    void getOne() {
        Message message = new Message();
        message.setId((long)1);
        Mockito.when(messageRepository.getOne((long)1)).thenReturn(message);
        assertEquals(messageRepository.getOne((long)1), message);
    }

    @Test
    void findAll() {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Message message = new Message();
            messages.add(message);
        }
        Mockito.when(messageRepository.findAll()).thenReturn(messages);
        assertEquals(messageRepository.findAll(), messages);
    }
}