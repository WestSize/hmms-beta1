package com.example.hmmsbeta1.web.repositories.WorkerRepositories;

import com.example.hmmsbeta1.web.entities.Worker;

import java.util.List;

public interface WorkerCustomRepository {
    List<Worker> showWorkersByCompanyId(Long id);
    Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId);
    Worker showWorkerByUserId(Long id);
}
