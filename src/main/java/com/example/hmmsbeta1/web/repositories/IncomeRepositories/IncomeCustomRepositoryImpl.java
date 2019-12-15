package com.example.hmmsbeta1.web.repositories.IncomeRepositories;

import com.example.hmmsbeta1.web.entities.Income;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class IncomeCustomRepositoryImpl implements IncomeCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Income> showAllIncomesByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from incomes as em where em.company_id like ?", Income.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<Income> showLast12IncomesByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from incomes as em where em.company_id like ? ORDER BY em.id DESC LIMIT 12", Income.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<Income> showAllIncomesByCompanyIdAndDate(Long id, String date) {
        Query query = entityManager.createNativeQuery("select  em.* from incomes as em where em.company_id like ? and income_date like ? order by em.id desc", Income.class);
        query.setParameter(1, id);
        query.setParameter(2, date);
        return query.getResultList();
    }
}
