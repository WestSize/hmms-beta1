package com.example.hmmsbeta1.web.services.CompanyServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public List showOnlyUsersCompanies(String email) {
        List<Company> companies = companyRepository.showOnlyUsersCompanies(email);
        return companies;
    }

    @Override
    public Company showOneUserCompany(String email) {
        Company company = companyRepository.showOneUserCompany(email);
        return company;
    }

    @Override
    public Company showCompanyByUserId(Long id) {
        Company company = showCompanyByUserId(id);
        return company;
    }

    @Override
    public void save(Company company) {
        companyRepository.save(company);
    }

    @Override
    public Company getOne(Long id) {
        Company company = companyRepository.getOne(id);
        return company;
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = companyRepository.findAll();
        return companies;
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
}
