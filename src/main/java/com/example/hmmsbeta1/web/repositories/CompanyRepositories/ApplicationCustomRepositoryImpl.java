package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Application;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ApplicationCustomRepositoryImpl implements ApplicationCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List showOnlyUsersCompanyApplications(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.company_id LIKE ?", Application.class);
        query.setParameter(1, id);
        return query.getResultList();
    }
}
