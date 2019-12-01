package com.example.hmmsbeta1.web.repositories;

import com.example.hmmsbeta1.web.entities.Message;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class MessageCustomRepositoryImpl implements  MessageCustomRepository{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List findAllByPrivateConversationId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM messages as em where em.conversation_id LIKE ?", Message.class);
        query.setParameter(1, id); //tuka moje da sa naloji da go napraq na id+"%"
        return query.getResultList();
    }
}
