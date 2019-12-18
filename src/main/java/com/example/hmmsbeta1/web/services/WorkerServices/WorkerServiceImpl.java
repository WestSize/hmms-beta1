package com.example.hmmsbeta1.web.services.WorkerServices;

import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {
    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public List<Worker> showWorkersByCompanyId(Long id) {
        List<Worker> workers = workerRepository.showWorkersByCompanyId(id);
        return workers;
    }

    @Override
    public Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId) {
        Worker worker = workerRepository.showWorkerByCompanyIdAndUserId(companyId, userId);
        return worker;
    }

    @Override
    public Worker showWorkerByUserId(Long id) {
        Worker worker = workerRepository.showWorkerByUserId(id);
        return worker;
    }

    @Override
    public void save(Worker worker) {
        workerRepository.save(worker);
    }

    @Override
    public Worker getOne(Long id) {
        Worker worker = workerRepository.getOne(id);
        return worker;
    }

    @Override
    public List<Worker> findAll() {
        List<Worker> workers = workerRepository.findAll();
        return workers;
    }

    @Override
    public void deleteById(Long id) {
        workerRepository.deleteById(id);
    }
}
