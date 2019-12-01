package com.example.hmmsbeta1.web.repositories;

import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository <Message, Long>, MessageCustomRepository {

}
