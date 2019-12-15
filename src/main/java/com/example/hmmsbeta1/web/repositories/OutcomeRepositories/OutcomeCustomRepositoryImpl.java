package com.example.hmmsbeta1.web.repositories.OutcomeRepositories;

import com.example.hmmsbeta1.web.entities.Outcome;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class OutcomeCustomRepositoryImpl implements OutcomeCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Outcome> findLast12OutcomesByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from outcomes as em where em.company_id like ? ORDER BY em.id DESC LIMIT 12", Outcome.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<Outcome> findAllOutcomesByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from outcomes as em where em.company_id like ? ORDER BY em.id", Outcome.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<Outcome> findOutcomesByCompanyIdAndDate(Long id, String date) {
        Query query = entityManager.createNativeQuery("select em.* from outcomes as em where em.company_id like ? and outcome_date like ? order by em.id", Outcome.class);
        query.setParameter(1, id);
        query.setParameter(2, date);
        return query.getResultList();
    }
}
