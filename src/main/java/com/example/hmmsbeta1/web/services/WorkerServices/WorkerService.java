package com.example.hmmsbeta1.web.services.WorkerServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.Worker;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WorkerService {
    List<Worker> showWorkersByCompanyId(Long id);
    Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId);
    Worker showWorkerByUserId(Long id);
    void save(Worker worker, Company company, User user);
    Worker getOne(Long id);
    List<Worker> findAll();
    void deleteById(Long id);
    void updateWorkerMonthWorkedDays(Worker worker, int days);
    void fire(Worker worker, User user, Long id);
    void updateFromUserPage(Worker worker, Worker workerOld);
    void addDayOff(Worker worker, Worker workerNew);
}
