package com.example.hmmsbeta1.web.services.CompanyServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationServiceImpl;
import com.example.hmmsbeta1.web.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompanyServiceImplTest {

    CompanyRepository companyRepository;
    UserService userService;
    CompanyServiceImpl service;

    @BeforeEach
    private void setupTest(){
        companyRepository = Mockito.mock(CompanyRepository.class);
        userService = Mockito.mock(UserService.class);
        service = new CompanyServiceImpl(companyRepository, userService);
    }

    @Test
    void showOnlyUsersCompanies() {
        List<Company> companies = new ArrayList<>();
        User user = new User();
        user.setEmail("gosho@gosho");
        String email = user.getEmail();
        for (int i = 0; i < 5; i++) {
            Company company = new Company();
            company.setUser(user);
            companies.add(company);
        }
        Mockito.when(companyRepository.showOnlyUsersCompanies(email)).thenReturn(companies);
        assertEquals(companyRepository.showOnlyUsersCompanies(email), companies);
    }

    @Test
    void showOneUserCompany() {
        User user = new User();
        user.setEmail("gosho@gosho");
        String email = user.getEmail();
        Company company = new Company();
        company.setUser(user);
        Mockito.when(companyRepository.showOneUserCompany(email)).thenReturn(company);
        assertEquals(companyRepository.showOneUserCompany(email), company);
    }

    @Test
    void showCompanyByUserId() {
        User user = new User();
        user.setId((long)1);
        Company company = new Company();
        company.setUser(user);
        Mockito.when(companyRepository.showCompanyByUserId((long)1)).thenReturn(company);
        assertEquals(companyRepository.showCompanyByUserId((long)1), company);
    }

    @Test
    void save() {
        Company company = new Company();
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "gosho";
            }
        };
        User user = new User();
        user.setEmail("gosho@gosho");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setPaydayDate(formatter.format(date));
        company.setProfit((company.getIncome() - company.getInvestment()));
        company.setCircularLetter("Здравейте! Поканен сте на интервю за работата, която сте кандидатствали" +
                " във фирмата '" + company.getName() + "'. Моля напишете ни удобен ден и час за среща в наш офис или " +
                "се свържете с нас на тел: (не е зададен). Поздрави, " + user.getFirstName() + " " + user.getLastName() + " (управител). :)");
        company.setOutcome(0);
        user.setWorkingStatus("owner");
//        Mockito.doThrow()
//        companyRepository.save(company);
    }

    @Test
    void update() {
    }

    @Test
    void fireWorker() {
    }

    @Test
    void updateCircullarLetter() {
    }

    @Test
    void setCompanyWorkersPlusOne() {
    }

    @Test
    void setCompanyIncomeAndProfitPlusOne() {
    }

    @Test
    void setCompanyIncomeAndProfitMinus() {
    }

    @Test
    void addCompanyOutcome() {
    }

    @Test
    void companyRemoveOutcome() {
    }

    @Test
    void companyPaySalaries() {
    }
}