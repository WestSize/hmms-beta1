package com.example.hmmsbeta1.web.repositories.MessagesRepositories;

import com.example.hmmsbeta1.web.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository <Message, Long>, MessageCustomRepository {

}
