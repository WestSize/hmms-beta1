package com.example.hmmsbeta1.web.services.SalaryServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Salary;
import com.example.hmmsbeta1.web.entities.Worker;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SalaryService {
    List<Salary> showWorkerSalariesByWorkerId(Long id);
    List<Salary> showLast12WorkerSalariesByWorkerId(Long id);
    void save(Company company, Worker worker, int year, int month);
    Salary getOne(Long id);
    List<Salary> findAll();
    void deleteById(Long id);
}
