package com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.WorkSchedule;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.security.Principal;
import java.util.List;

public class WorkScheduleCustomRepositoryImpl implements WorkScheduleCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Override
    public List<WorkSchedule> showWorkScheduleByCompanyId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM work_schedules AS em WHERE em.company_id LIKE ?", WorkSchedule.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<WorkSchedule> showWorkScheduleOfCompanyByDateAndCompanyId(String date, Long Id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM work_schedules AS em WHERE em.company_id LIKE ? AND em.date = ?", WorkSchedule.class);
        query.setParameter(1, Id);
        query.setParameter(2, date);
        return query.getResultList();
    }

    @Override
    public List<WorkSchedule> showLast12WorkSchedulesByUserId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* from work_schedules as em where em.worker_id LIKE ? ORDER BY em.id DESC LIMIT 12", WorkSchedule.class);
        Worker worker = workerRepository.showWorkerByUserId(id);
        query.setParameter(1, worker.getId());
        return query.getResultList();
    }

    @Override
    public List<WorkSchedule> showAllWorkScheduleByUserId(Long id) {
        Query query = entityManager.createNativeQuery("SELECT em.* from work_schedules as em where em.worker_id LIKE ? ORDER BY em.id DESC", WorkSchedule.class);
        Worker worker = workerRepository.showWorkerByUserId(id);
        query.setParameter(1, worker.getId());
        return query.getResultList();
    }

}
