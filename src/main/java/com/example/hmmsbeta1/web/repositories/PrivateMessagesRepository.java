package com.example.hmmsbeta1.web.repositories;

import com.example.hmmsbeta1.web.entities.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PrivateMessagesRepository extends JpaRepository<PrivateMessage, Long> {

}
