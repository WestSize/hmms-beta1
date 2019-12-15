package com.example.hmmsbeta1.web.repositories.SalaryRepositories;

import com.example.hmmsbeta1.web.entities.Salary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class SalaryCustomRepositoryImpl implements SalaryCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Salary> showWorkerSalariesByWorkerId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from salaries as em where em.worker_id like ?", Salary.class);
        query.setParameter(1, id);
        return query.getResultList();
    }
}
