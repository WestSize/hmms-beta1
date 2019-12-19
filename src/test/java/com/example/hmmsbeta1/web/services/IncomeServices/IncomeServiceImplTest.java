package com.example.hmmsbeta1.web.services.IncomeServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.IncomeRepositories.IncomeRepository;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationServiceImpl;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncomeServiceImplTest {

    IncomeRepository incomeRepository;
    private IncomeServiceImpl service;

    @BeforeEach
    private void setupTest(){
        incomeRepository = Mockito.mock(IncomeRepository.class);
        service = new IncomeServiceImpl(incomeRepository);

    }

    @Test
    void showAllIncomesByCompanyId() {
        List<Income> incomes = new ArrayList<>();
        Company company = new Company();
        company.setId((long)1);
        for (int i = 0; i < 5; i++) {
            Income income = new Income();
            income.setCompany(company);
            incomes.add(income);
        }
        Mockito.when(incomeRepository.showAllIncomesByCompanyId((long)1)).thenReturn(incomes);
        assertEquals(incomeRepository.showAllIncomesByCompanyId((long)1), incomes);
    }

    @Test
    void showLast12IncomesByCompanyIdIfTheyAre12() {
        List<Income> incomes = new ArrayList<>();
        Company company = new Company();
        company.setId((long)1);
        for (int i = 0; i < 12; i++) {
            Income income = new Income();
            income.setCompany(company);
            incomes.add(income);
        }
        Mockito.when(incomeRepository.showAllIncomesByCompanyId((long)1)).thenReturn(incomes);
        assertEquals(incomeRepository.showAllIncomesByCompanyId((long)1).size(), incomes.size());
    }

    @Test
    void showLast12IncomesByCompanyIdIfTheyAreBiggerThan12() {
        List<Income> incomes = new ArrayList<>();
        Company company = new Company();
        company.setId((long)1);
        for (int i = 0; i < 13; i++) {
            Income income = new Income();
            income.setCompany(company);
            incomes.add(income);
        }
        Mockito.when(incomeRepository.showLast12IncomesByCompanyId((long)1)).thenReturn(incomes);
        assertEquals(12, incomeRepository.showLast12IncomesByCompanyId((long)1).size()-1);
    }

    @Test
    void showAllIncomesByCompanyIdAndDate() {
        Company company = new Company();
        company.setId((long)1);
        List<Income> incomes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Income income = new Income();
            income.setCompany(company);
            income.setIncomeDate("data");
            incomes.add(income);
        }
        Mockito.when(incomeRepository.showAllIncomesByCompanyIdAndDate((long)1, "data")).thenReturn(incomes);
        assertEquals(incomeRepository.showAllIncomesByCompanyIdAndDate((long)1, "data"), incomes);
    }

    @Test
    void getOne() {
        Income income = new Income();
        income.setId((long)1);
        Mockito.when(incomeRepository.getOne((long)1)).thenReturn(income);
        assertEquals(incomeRepository.getOne((long)1), income);
    }

    @Test
    void findAll() {
        List<Income> incomes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Income income = new Income();
            incomes.add(income);
        }
        Mockito.when(incomeRepository.findAll()).thenReturn(incomes);
        assertEquals(incomeRepository.findAll(), incomes);
    }
}