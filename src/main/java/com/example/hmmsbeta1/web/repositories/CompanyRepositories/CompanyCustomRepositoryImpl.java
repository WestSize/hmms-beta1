package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.PrivateConversation;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class CompanyCustomRepositoryImpl implements CompanyCustomRepository{
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Override
    public List showOnlyUsersCompanies(String email) {
        Query query = entityManager.createNativeQuery("SELECT * FROM companies as em where em.owner_id LIKE ?", Company.class);
        Long userId = userRepository.findByEmail(email).getId();
        query.setParameter(1, userId);
        return query.getResultList();
    }
}
