package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Worker;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class WorkerCustomRepositoryImpl implements WorkerCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Worker> showWorkersByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM workers as em WHERE em.company_id LIKE ?", Worker.class);
        query.setParameter(1, id);
        return query.getResultList();
    }
}
