package com.example.hmmsbeta1.web.services.CompanyServices;

import com.example.hmmsbeta1.web.entities.Company;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CompanyService {
    List showOnlyUsersCompanies(String email);
    Company showOneUserCompany(String email);
    Company showCompanyByUserId(Long id);
    void save(Company company);
    Company getOne(Long id);
    List<Company> findAll();
    void deleteById(Long id);
}
