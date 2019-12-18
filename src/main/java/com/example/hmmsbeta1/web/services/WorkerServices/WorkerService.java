package com.example.hmmsbeta1.web.services.WorkerServices;

import com.example.hmmsbeta1.web.entities.Worker;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WorkerService {
    List<Worker> showWorkersByCompanyId(Long id);
    Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId);
    Worker showWorkerByUserId(Long id);
    void save(Worker worker);
    Worker getOne(Long id);
    List<Worker> findAll();
    void deleteById(Long id);
}
