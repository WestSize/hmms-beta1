package com.example.hmmsbeta1.web.repositories.ApplicationRepositories;

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
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.company_id LIKE ? and em.invited = false", Application.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List showApplicationByCandidateId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.candidate_id LIKE ?", Application.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List showOnlyUsersCompanyApplicationsInvited(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.company_id LIKE ? and em.invited = ?", Application.class);
        query.setParameter(1, id);
        query.setParameter(2, true);
        return query.getResultList();
    }

    @Override
    public Application showApplicationByOwnerCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.company_id LIKE ?", Application.class);
        query.setParameter(1, id);
        return (Application) query.getSingleResult();
    }

    @Override
    public Application showApplicationByCompanyId(Long id) {
        return null;
    }

    @Override
    public Application showOneApplicationByUserIdAndCompanyId(Long UserId, Long CompanyID) {
        Query query = entityManager.createNativeQuery("SELECT em.* FROM applications as em where em.candidate_id LIKE ? and em.company_id LIKE ?", Application.class);
        query.setParameter(1, UserId);
        query.setParameter(2, CompanyID);
        return (Application) query.getSingleResult();
    }

}
