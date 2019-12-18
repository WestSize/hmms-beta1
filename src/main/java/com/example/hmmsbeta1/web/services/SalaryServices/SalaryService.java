package com.example.hmmsbeta1.web.services.SalaryServices;

import com.example.hmmsbeta1.web.entities.Salary;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SalaryService {
    List<Salary> showWorkerSalariesByWorkerId(Long id);
    List<Salary> showLast12WorkerSalariesByWorkerId(Long id);
    void save(Salary salary);
    Salary getOne(Long id);
    List<Salary> findAll();
    void deleteById(Long id);
}
