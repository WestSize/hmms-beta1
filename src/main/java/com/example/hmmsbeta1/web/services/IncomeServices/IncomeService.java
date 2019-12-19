package com.example.hmmsbeta1.web.services.IncomeServices;


import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Income;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IncomeService {
    List<Income> showAllIncomesByCompanyId(Long id);
    List<Income> showLast12IncomesByCompanyId(Long id);
    List<Income> showAllIncomesByCompanyIdAndDate(Long id, String date);
    void save(Company company);
    void addNew(Income income, Company company);
    Income getOne(Long id);
    List<Income> findAll();
    void deleteById(Long id);
}
