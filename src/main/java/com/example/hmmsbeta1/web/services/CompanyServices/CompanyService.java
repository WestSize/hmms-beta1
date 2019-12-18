package com.example.hmmsbeta1.web.services.CompanyServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.entities.Outcome;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

public interface CompanyService {
    List showOnlyUsersCompanies(String email);
    Company showOneUserCompany(String email);
    Company showCompanyByUserId(Long id);
    void save(Company company, Principal principal);
    Company getOne(Long id);
    List<Company> findAll();
    void deleteById(Long id);
    void update(Company company, Long companyId);
    void fireWorker(Company company);
    void updateCircullarLetter(Company company, Principal principal);
    void setCompanyWorkersPlusOne(Company company);
    void setCompanyIncomeAndProfitPlusOne(Company company, Income income);
    void setCompanyIncomeAndProfitMinus(Company company, Income income);
    void addCompanyOutcome(Company company, Outcome outcome);
    void companyRemoveOutcome(Company company, Outcome outcome);
    void companyPaySalaries(Company company, int SalarySum);
}
