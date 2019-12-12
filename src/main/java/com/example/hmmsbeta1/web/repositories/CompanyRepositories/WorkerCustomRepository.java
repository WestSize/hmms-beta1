package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Worker;

import java.util.List;

public interface WorkerCustomRepository {
    List<Worker> showWorkersByCompanyId(Long id);
}
