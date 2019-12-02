package com.example.hmmsbeta1.web.repositories.MessagesRepositories;

import com.example.hmmsbeta1.web.entities.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Long>, PrivateConversationCustomRepository {

}
