package com.example.hmmsbeta1.web.repositories.MessagesRepositories;

import com.example.hmmsbeta1.web.entities.PrivateConversation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class PrivateConversationCustomRepositoryImpl implements PrivateConversationCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<PrivateConversation> findAllUsersConversationByEmail(String email) {
        Query query = entityManager.createNativeQuery("SELECT * FROM private_conversation as em where em.user_recipient LIKE ? or em.user_sender LIKE ? ORDER BY date_and_time desc", PrivateConversation.class);
        query.setParameter(1, email);
        query.setParameter(2, email);
        return query.getResultList();
    }
}
