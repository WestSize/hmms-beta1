package com.example.hmmsbeta1.web.repositories.SalaryRepositories;

import com.example.hmmsbeta1.web.entities.Salary;

import java.util.List;

public interface SalaryCustomRepository {
    List<Salary> showWorkerSalariesByWorkerId(Long id);
}
