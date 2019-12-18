package com.example.hmmsbeta1.web.services.SalaryServices;

import com.example.hmmsbeta1.web.entities.Salary;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;

    @Override
    public List<Salary> showWorkerSalariesByWorkerId(Long id) {
        List<Salary> salaries = salaryRepository.showWorkerSalariesByWorkerId(id);
        return salaries;
    }

    @Override
    public List<Salary> showLast12WorkerSalariesByWorkerId(Long id) {
        List<Salary> salaries = salaryRepository.showLast12WorkerSalariesByWorkerId(id);
        return salaries;
    }

    @Override
    public void save(Salary salary) {
        salaryRepository.save(salary);
    }

    @Override
    public Salary getOne(Long id) {
        Salary salary = salaryRepository.getOne(id);
        return salary;
    }

    @Override
    public List<Salary> findAll() {
        List<Salary> salaries = salaryRepository.findAll();
        return salaries;
    }

    @Override
    public void deleteById(Long id) {
        salaryRepository.deleteById(id);
    }
}
