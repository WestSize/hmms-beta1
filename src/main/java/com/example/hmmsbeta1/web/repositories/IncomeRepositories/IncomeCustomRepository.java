package com.example.hmmsbeta1.web.repositories.IncomeRepositories;

import com.example.hmmsbeta1.web.entities.Income;

import java.util.List;

public interface IncomeCustomRepository {
    List<Income> showAllIncomesByCompanyId(Long id);
    List<Income> showLast12IncomesByCompanyId(Long id);
    List<Income> showAllIncomesByCompanyIdAndDate(Long id, String date);
}
