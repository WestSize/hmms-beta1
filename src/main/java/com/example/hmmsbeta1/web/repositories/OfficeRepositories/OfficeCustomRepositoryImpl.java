package com.example.hmmsbeta1.web.repositories.OfficeRepositories;

import com.example.hmmsbeta1.web.entities.Office;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class OfficeCustomRepositoryImpl implements OfficeCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Office> showOfficesByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("select em.* from offices as em where em.company_id like ?", Office.class);
        query.setParameter(1, id);
        return query.getResultList();
    }
}
