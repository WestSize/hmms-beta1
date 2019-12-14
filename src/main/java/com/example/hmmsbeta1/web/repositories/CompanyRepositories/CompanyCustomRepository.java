package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Company;

import java.util.List;

public interface CompanyCustomRepository {
    List showOnlyUsersCompanies(String email);
    Company showOneUserCompany(String email);
    Company showCompanyByUserId(Long id);
}
