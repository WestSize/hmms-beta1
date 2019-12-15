package com.example.hmmsbeta1.web.repositories.IncomeRepositories;

import com.example.hmmsbeta1.web.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long>, IncomeCustomRepository {
}
