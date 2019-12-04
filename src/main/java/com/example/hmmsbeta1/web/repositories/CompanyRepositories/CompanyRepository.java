package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyCustomRepository {
}
