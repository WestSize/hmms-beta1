package com.example.hmmsbeta1.web.repositories.WorkerRepositories;

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

    @Override
    public Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM workers AS em WHERE em.company_id LIKE ? AND em.user_id LIKE ?", Worker.class);
        query.setParameter(1, companyId);
        query.setParameter(2, userId);
        return (Worker) query.getSingleResult();
    }

    @Override
    public Worker showWorkerByUserId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* from workers AS em WHERE em.user_id LIKE ?", Worker.class);
        query.setParameter(1, id);
        return (Worker) query.getSingleResult();
    }
}
