package com.example.hmmsbeta1.web.services.IncomeServices;

import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.repositories.IncomeRepositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Override
    public List<Income> showAllIncomesByCompanyId(Long id) {
        List<Income> incomes = incomeRepository.showAllIncomesByCompanyId(id);
        return incomes;
    }

    @Override
    public List<Income> showLast12IncomesByCompanyId(Long id) {
        List<Income> incomes = incomeRepository.showLast12IncomesByCompanyId(id);
        return incomes;
    }

    @Override
    public List<Income> showAllIncomesByCompanyIdAndDate(Long id, String date) {
        List<Income> incomes = incomeRepository.showAllIncomesByCompanyIdAndDate(id, date);
        return incomes;
    }

    @Override
    public void save(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public Income getOne(Long id) {
        Income income = incomeRepository.getOne(id);
        return income;
    }

    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        incomeRepository.deleteById(id);
    }
}
